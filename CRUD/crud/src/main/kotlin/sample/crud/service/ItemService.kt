package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
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
    fun get(id: Long) : Item? = itemRepository.findById(id).orElse(null)

    @Transactional(readOnly = true)
    fun getItemGetResponse(id: Long) : ItemGetResponse? {
        val item = get(id) ?: return null
        return ItemGetResponse(
            id = id,
            name = item.name,
            price = item.price,
            quantity = item.quantity,
        )
    }

    @Transactional
    fun save(request: ItemSaveRequest) : Long {
        validateItemSave(request.name)

        return itemRepository.save(Item(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )).id
    }

    @Transactional
    fun update(id: Long, request: ItemUpdateRequest) {
        validateItemUpdate(request.name)

        val item = get(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM)
        item.update(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )
    }

    @Transactional
    fun delete(id: Long) = itemRepository.delete(get(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM))

    private fun validateItemSave(name: String) {
        if (isDuplicatedName(name)) throw CustomException(ErrorCode.DUPLICATED_ITEM_NAME)
    }

    private fun validateItemUpdate(name: String?) {
        if (name == null) return
        if (isDuplicatedName(name)) throw CustomException(ErrorCode.DUPLICATED_ITEM_NAME)
    }

    private fun isDuplicatedName(name: String) : Boolean = itemRepository.existsByName(name)
}