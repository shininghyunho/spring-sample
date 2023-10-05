package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.crud.controller.dto.Item.ItemGetResponse
import sample.crud.controller.dto.Item.ItemSaveRequest
import sample.crud.controller.dto.Item.ItemUpdateRequest
import sample.crud.entity.Item
import sample.crud.repository.ItemRepository

@Service
class ItemService (
    private val itemRepository: ItemRepository
) {
    @Transactional(readOnly = true)
    fun get(id: Long) : Item {
        return itemRepository.findById(id).orElseThrow()
    }

    @Transactional(readOnly = true)
    fun getItemGetResponse(id: Long) : ItemGetResponse {
        return ItemGetResponse(
            id = id,
            name = get(id).name,
            price = get(id).price,
            quantity = get(id).quantity,
        )
    }

    @Transactional
    fun save(request: ItemSaveRequest) : Long {
        return itemRepository.save(Item(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )).id
    }

    @Transactional
    fun update(id: Long, request: ItemUpdateRequest) {
        val item = get(id)
        item.update(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )
    }

    @Transactional
    fun delete(id: Long) {
        itemRepository.deleteById(id)
    }
}