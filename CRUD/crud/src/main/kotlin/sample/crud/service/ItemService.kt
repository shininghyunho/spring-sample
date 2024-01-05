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
    fun getEntity(id: Long) : Item? = itemRepository.findById(id).orElse(null)

    @Transactional(readOnly = true)
    fun get(id: Long) : ItemGetResponse? {
        val item = getEntity(id) ?: return null
        return ItemGetResponse(
            id = id,
            name = item.name,
            price = item.price,
            quantity = item.quantity,
        )
    }

    @Transactional
    fun save(request: ItemSaveRequest) : Long {
        validateItemSave(request)

        return itemRepository.save(Item(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )).id
    }

    @Transactional
    fun update(id: Long, request: ItemUpdateRequest) {
        validateItemUpdate(request)

        val item = getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM)
        item.update(
            name = request.name,
            price = request.price,
            quantity = request.quantity,
        )
    }

    @Transactional
    fun delete(id: Long) = itemRepository.delete(getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM))

    @Transactional
    fun increaseQuantity(id: Long, quantity: Int) {
        val item = getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM)
        item.update(quantity = item.quantity + quantity)
    }

    @Transactional
    fun decreaseQuantity(id: Long, quantity: Int) {
        val item = getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_ITEM)
        if(item.quantity < quantity) throw CustomException(ErrorCode.NOT_ENOUGH_ITEM_QUANTITY)
        item.update(quantity = item.quantity - quantity)
    }

    private fun validateItemSave(request: ItemSaveRequest) {
        if (itemRepository.existsByName(request.name)) throw CustomException(ErrorCode.DUPLICATED_ITEM_NAME)
        if (request.price < 0) throw CustomException(ErrorCode.INVALID_PRICE)
        if (request.quantity < 0) throw CustomException(ErrorCode.INVALID_QUANTITY)
    }

    private fun validateItemUpdate(request: ItemUpdateRequest) {
        request.name?.let { if (itemRepository.existsByName(it)) throw CustomException(ErrorCode.DUPLICATED_ITEM_NAME) }
        request.price?.let { if (it < 0) throw CustomException(ErrorCode.INVALID_PRICE) }
        request.quantity?.let { if (it < 0) throw CustomException(ErrorCode.INVALID_QUANTITY) }
    }
}