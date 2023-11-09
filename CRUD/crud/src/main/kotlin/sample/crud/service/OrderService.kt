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

    @Transactional
    fun delete(id: Long) = orderRepository.delete(getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ORDER))
}