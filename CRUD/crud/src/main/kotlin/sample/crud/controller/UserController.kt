package sample.crud.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sample.crud.controller.dto.user.UserGetResponse
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.controller.dto.user.UserUpdateRequest
import sample.crud.service.UserService

@RestController
class UserController (
    private val userService: UserService
) {
    @PostMapping("/users")
    fun save(request: UserSaveRequest) : ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("id:"+userService.save(request))
    }

    @GetMapping("/users")
    fun get(@RequestParam id: Long) : ResponseEntity<UserGetResponse> {
        return ResponseEntity.ok(userService.getUserGetResponse(id))
    }

    @PutMapping("/users")
    fun update(@RequestParam id: Long, request: UserUpdateRequest) : ResponseEntity<String> {
        userService.update(id, request)
        return ResponseEntity.ok("success")
    }

    @DeleteMapping("/users")
    fun delete(@RequestParam id: Long) : ResponseEntity<String> {
        userService.delete(id)
        return ResponseEntity.ok("success")
    }
}