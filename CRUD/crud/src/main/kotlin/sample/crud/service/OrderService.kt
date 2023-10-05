package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        // user id 로 user 조회
        val user = userService.getUser(request.userId)
        // order 저장
        val order = orderRepository.save(Order(user = user))
        // order item 저장
        request.items.forEach {
            val item = itemService.getItem(it.id)
            val orderItem = orderItemService.getOrderItem(orderItemService.save(order, item, it.count))
            // order 에 order item 저장
            order.orderItems.add(orderItem)
        }
        return order.id
    }
}