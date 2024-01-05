package sample.crud.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sample.crud.common.response.CustomBody
import sample.crud.common.response.CustomResponse
import sample.crud.common.response.SuccessBody
import sample.crud.controller.dto.order.OrderSaveRequest
import sample.crud.service.OrderService

@RestController
class OrderController (
    private val orderService: OrderService
) {
    @PostMapping("/orders")
    fun save(@RequestBody request: OrderSaveRequest) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = orderService.save(request),
                status = HttpStatus.CREATED,
                message = "주문 생성 성공"
            )
        ).toResponseEntity()
    }

    @GetMapping("/orders/{id}")
    fun get(@PathVariable id: Long) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = orderService.get(id),
                status = HttpStatus.OK,
                message = "주문 조회 성공"
            )
        ).toResponseEntity()
    }

    @DeleteMapping("/orders/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<CustomBody> {
        orderService.delete(id)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "주문 삭제 성공"
            )
        ).toResponseEntity()
    }

    @PutMapping("/orders/{id}/cancel")
    fun cancel(@PathVariable id: Long) : ResponseEntity<CustomBody> {
        orderService.cancel(id)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "주문 취소 성공"
            )
        ).toResponseEntity()
    }
}