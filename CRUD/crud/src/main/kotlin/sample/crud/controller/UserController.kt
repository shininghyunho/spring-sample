package sample.crud.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sample.crud.common.response.CustomBody
import sample.crud.common.response.CustomResponse
import sample.crud.common.response.SuccessBody
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.controller.dto.user.UserUpdateRequest
import sample.crud.service.UserService

@RestController
class UserController (
    private val userService: UserService
) {
    @PostMapping("/users")
    fun save(request: UserSaveRequest) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = userService.save(request),
                status = HttpStatus.CREATED,
                message = "유저 생성 성공"
            )
        ).toResponseEntity()
    }

    @GetMapping("/users")
    fun get(@RequestParam id: Long) : ResponseEntity<CustomBody> {
        return CustomResponse(
            body = SuccessBody(
                data = userService.getUserGetResponse(id),
                status = HttpStatus.OK,
                message = "유저 조회 성공"
            )
        ).toResponseEntity()
    }

    @PutMapping("/users")
    fun update(@RequestParam id: Long, request: UserUpdateRequest) : ResponseEntity<CustomBody> {
        userService.update(id, request)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "유저 수정 성공"
            )
        ).toResponseEntity()
    }

    @DeleteMapping("/users")
    fun delete(@RequestParam id: Long) : ResponseEntity<CustomBody> {
        userService.delete(id)
        return CustomResponse(
            body = SuccessBody(
                status = HttpStatus.OK,
                message = "유저 삭제 성공"
            )
        ).toResponseEntity()
    }
}