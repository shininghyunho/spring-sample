package sample.crud.repository

import org.springframework.data.jpa.repository.JpaRepository
import sample.crud.entity.Item

interface ItemRepository : JpaRepository<Item, Long> {
    fun existsByName(name: String): Boolean
}