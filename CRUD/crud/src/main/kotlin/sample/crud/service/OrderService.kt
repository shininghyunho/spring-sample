package sample.crud.service

import org.springframework.stereotype.Service
import sample.crud.repository.OrderRepository

@Service
class OrderService (
    private val orderRepository: OrderRepository
) {
    // save order
    fun save() {

    }
}