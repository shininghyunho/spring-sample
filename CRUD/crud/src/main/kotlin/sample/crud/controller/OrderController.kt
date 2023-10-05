package sample.crud.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import sample.crud.controller.dto.order.OrderGetResponse
import sample.crud.controller.dto.order.OrderSaveRequest
import sample.crud.service.OrderService

@RestController
class OrderController (
    private val orderService: OrderService
) {
    @PostMapping("/orders")
    fun save(request: OrderSaveRequest) : ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("id:"+orderService.save(request))
    }

    @GetMapping("/orders")
    fun get(id: Long) : ResponseEntity<OrderGetResponse> {
        return ResponseEntity.ok(orderService.getOrderGetResponse(id))
    }

    @DeleteMapping("/orders")
    fun delete(id: Long) : ResponseEntity<String> {
        orderService.delete(id)
        return ResponseEntity.ok("success")
    }
}