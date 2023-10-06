package sample.crud.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import sample.crud.common.response.CustomBody
import sample.crud.common.response.CustomResponse
import sample.crud.common.response.SuccessBody
import sample.crud.controller.dto.order.OrderGetResponse
import sample.crud.controller.dto.order.OrderSaveRequest
import sample.crud.service.OrderService

@RestController
class OrderController (
    private val orderService: OrderService
) {
    @PostMapping("/orders")
    fun save(request: OrderSaveRequest) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = orderService.save(request),
                status = HttpStatus.CREATED,
                message = "주문 생성 성공"
            )
        ).toResponseEntity()
    }

    @GetMapping("/orders")
    fun get(id: Long) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = orderService.getOrderGetResponse(id),
                status = HttpStatus.OK,
                message = "주문 조회 성공"
            )
        ).toResponseEntity()
    }

    @DeleteMapping("/orders")
    fun delete(id: Long) : ResponseEntity<CustomBody> {
        orderService.delete(id)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "주문 삭제 성공"
            )
        ).toResponseEntity()
    }
}