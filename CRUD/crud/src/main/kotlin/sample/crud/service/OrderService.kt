package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        // user id 로 user 조회
        val user = userService.get(request.userId)
        // order 저장
        val order = orderRepository.save(Order(user = user))
        // order item 저장
        request.items.forEach {
            val item = itemService.get(it.id)
            val orderItem = orderItemService.get(orderItemService.save(order, item, it.count))
            // order 에 order item 저장
            order.orderItems.add(orderItem)
        }
        return order.id
    }

    @Transactional(readOnly = true)
    fun get(id: Long) : Order {
        return orderRepository.findById(id).orElseThrow()
    }

    @Transactional(readOnly = true)
    fun getOrderGetResponse(id: Long) : OrderGetResponse {
        val order = get(id)
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
    fun delete(id: Long) {
        orderRepository.deleteById(id)
    }
}