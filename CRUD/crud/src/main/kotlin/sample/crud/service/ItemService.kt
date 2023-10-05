package sample.crud.service

import org.springframework.stereotype.Service
import sample.crud.entity.Item
import sample.crud.repository.ItemRepository

@Service
class ItemService (
    private val itemRepository: ItemRepository
) {
    fun getItem(id: Long) : Item {
        return itemRepository.findById(id).orElseThrow()
    }
}