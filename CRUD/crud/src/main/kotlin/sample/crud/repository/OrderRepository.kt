package sample.crud.repository

import org.springframework.data.jpa.repository.JpaRepository
import sample.crud.entity.Order

interface OrderRepository : JpaRepository<Order, Long>