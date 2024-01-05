package sample.crud.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sample.crud.common.response.CustomBody
import sample.crud.common.response.CustomResponse
import sample.crud.common.response.SuccessBody
import sample.crud.controller.dto.Item.ItemSaveRequest
import sample.crud.controller.dto.Item.ItemUpdateRequest
import sample.crud.service.ItemService

@RestController
class ItemController (
    private val itemService: ItemService
) {
    @PostMapping("/items")
    fun save(@RequestBody request: ItemSaveRequest) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = itemService.save(request),
                status = HttpStatus.CREATED,
                message = "아이템 생성 성공"
            )
        ).toResponseEntity()
    }

    @GetMapping("/items/{id}")
    fun get(@PathVariable id: Long) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = itemService.get(id),
                status = HttpStatus.OK,
                message = "아이템 조회 성공"
            )
        ).toResponseEntity()
    }

    @PutMapping("/items/{id}")
    fun update(@PathVariable id: Long,@RequestBody request: ItemUpdateRequest) : ResponseEntity<CustomBody> {
        itemService.update(id, request)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "아이템 수정 성공"
            )
        ).toResponseEntity()
    }

    @DeleteMapping("/items/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<CustomBody> {
        itemService.delete(id)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "아이템 삭제 성공"
            )
        ).toResponseEntity()
    }
}