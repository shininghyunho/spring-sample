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

        val item = get(id) ?: throw CustomException(errorCode = ErrorCode.NOT_FOUND, message = "존재하지 않는 아이템입니다.")
        item.update(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )
    }

    @Transactional
    fun delete(id: Long) = itemRepository.delete(get(id) ?: throw CustomException(errorCode = ErrorCode.NOT_FOUND, message = "존재하지 않는 아이템입니다."))

    private fun validateItemSave(name: String) {
        if (isDuplicateName(name)) throw CustomException(errorCode = ErrorCode.DUPLICATED_VALUE, message = "중복된 이름입니다.")
    }

    private fun validateItemUpdate(name: String?) {
        if (name == null) return
        if (isDuplicateName(name)) throw CustomException(errorCode = ErrorCode.DUPLICATED_VALUE, message = "중복된 이름입니다.")
    }

    private fun isDuplicateName(name: String) : Boolean = itemRepository.existsByName(name)
}