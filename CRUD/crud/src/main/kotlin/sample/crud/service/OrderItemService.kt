package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.crud.entity.Item
import sample.crud.entity.Order
import sample.crud.entity.OrderItem
import sample.crud.repository.OrderItemRepository

@Service
class OrderItemService (
    private val orderItemRepository: OrderItemRepository
) {
    @Transactional
    fun save(order: Order, item: Item, count: Int) : Long {
        return orderItemRepository.save(OrderItem(order = order, item = item, count = count)).id
    }

    @Transactional(readOnly = true)
    fun get(id: Long) : OrderItem {
        return orderItemRepository.findById(id).orElseThrow()
    }

    @Transactional
    fun delete(id: Long) {
        orderItemRepository.deleteById(id)
    }
}