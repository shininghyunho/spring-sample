package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.controller.dto.order.OrderGetResponse
import sample.crud.controller.dto.order.OrderSaveRequest
import sample.crud.entity.Order
import sample.crud.repository.OrderRepository

@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val userService: UserService,
    private val itemService: ItemService,
    private val orderItemService: OrderItemService,
) {
    // save order
    @Transactional
    fun save(request: OrderSaveRequest) : Long {
        val user = userService.getEntity(request.userId) ?: throw CustomException(ErrorCode.NOT_EXISTED_USER)
        val order = orderRepository.save(Order(user = user))
        request.items.forEach {
            val item = itemService.getEntity(it.id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM)
            orderItemService.save(order, item, it.count)
            itemService.decreaseQuantity(item.id, it.count)
        }
        return order.id
    }

    @Transactional(readOnly = true)
    fun getEntity(id: Long) : Order? = orderRepository.findById(id).orElse(null)


    @Transactional(readOnly = true)
    fun get(id: Long) : OrderGetResponse? {
        val order = getEntity(id) ?: return null
        return OrderGetResponse(
            id = order.id,
            userId = order.user.id,
            items = order.orderItems.map {
                OrderGetResponse.Item(
                    id = it.item.id,
                    name = it.item.name,
                    price = it.item.price,
                    count = it.count,
                )
            }
        )
    }

    /**
     * 주문 삭제 : 주문이 완료되었을때 호출
     */
    @Transactional
    fun delete(id: Long) = orderRepository.delete(getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ORDER))

    /**
     * 주문 취소 : 주문 취소시 주문 상품의 재고를 다시 증가시켜야 한다.
     */
    @Transactional
    fun cancel(id: Long) {
        val order = getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ORDER)
        order.orderItems.forEach { itemService.increaseQuantity(it.item.id, it.count) }
        orderRepository.delete(order)
    }


}