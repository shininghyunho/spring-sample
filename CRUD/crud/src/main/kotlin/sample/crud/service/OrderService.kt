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
        val user = userService.get(request.userId) ?: throw CustomException(errorCode = ErrorCode.NOT_FOUND, message = "존재하지 않는 유저입니다.")
        val order = orderRepository.save(Order(user = user))
        // order 에 새로운 orderItem 추가
        request.items.forEach {
            val item = itemService.get(it.id) ?: throw CustomException(errorCode = ErrorCode.NOT_FOUND, message = "존재하지 않는 아이템입니다.")
            val orderItem = orderItemService.get(orderItemService.save(order, item, it.count)) ?: throw CustomException(errorCode = ErrorCode.NOT_FOUND, message = "존재하지 않는 주문 아이템입니다.")
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
    fun delete(id: Long) = orderRepository.delete(get(id) ?: throw CustomException(errorCode = ErrorCode.NOT_FOUND, message = "존재하지 않는 주문입니다."))
}