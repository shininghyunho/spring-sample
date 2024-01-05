package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.crud.entity.Item
import sample.crud.entity.Order
import sample.crud.entity.OrderItem
import sample.crud.repository.OrderItemRepository

@Service
class OrderItemService (
    private val orderItemRepository: OrderItemRepository,
) {
    @Transactional
    fun save(order: Order, item: Item, count: Int) : Long = orderItemRepository.save(OrderItem(order = order,item = item, count = count)).id

    @Transactional(readOnly = true)
    fun getEntity(id: Long) : OrderItem? = orderItemRepository.findById(id).orElse(null)
}