package sample.crud.repository

import org.springframework.data.jpa.repository.JpaRepository
import sample.crud.entity.OrderItem

interface OrderItemRepository : JpaRepository<OrderItem, Long>