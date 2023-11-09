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
        // order 에 새로운 orderItem 추가
        request.items.forEach {
            val item = itemService.getEntity(it.id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM)
            val orderItem = orderItemService.get(orderItemService.save(order, item, it.count)) ?: throw CustomException(ErrorCode.NOT_EXISTED_ORDER_ITEM)
            order.orderItems.add(orderItem)
        }
        return order.id
    }

    @Transactional(readOnly = true)
    fun get(id: Long) : Order? = orderRepository.findById(id).orElse(null)


    @Transactional(readOnly = true)
    fun getOrderGetResponse(id: Long) : OrderGetResponse? {
        val order = get(id) ?: return null
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

    @Transactional
    fun delete(id: Long) = orderRepository.delete(get(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ORDER))
}