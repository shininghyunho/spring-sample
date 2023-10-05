package sample.crud.controller.dto

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sample.crud.controller.dto.Item.ItemGetResponse
import sample.crud.controller.dto.Item.ItemSaveRequest
import sample.crud.controller.dto.Item.ItemUpdateRequest
import sample.crud.service.ItemService

@RestController
class ItemController (
    private val itemService: ItemService
) {
    @PostMapping("/items")
    fun save(request: ItemSaveRequest) : ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("id:"+itemService.save(request))
    }

    @GetMapping("/items")
    fun get(@RequestParam id: Long) : ResponseEntity<ItemGetResponse> {
        return ResponseEntity.ok(itemService.getItemGetResponse(id))
    }

    @PutMapping("/items")
    fun update(@RequestParam id: Long, request: ItemUpdateRequest) : ResponseEntity<String> {
        itemService.update(id, request)
        return ResponseEntity.ok("success")
    }

    @DeleteMapping("/items")
    fun delete(@RequestParam id: Long) : ResponseEntity<String> {
        itemService.delete(id)
        return ResponseEntity.ok("success")
    }
}