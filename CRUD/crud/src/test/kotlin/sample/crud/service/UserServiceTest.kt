package sample.crud.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.repository.UserRepository

class UserServiceTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>() // mock 객체 생성
    val userService = UserService(userRepository) // mock 객체를 주입하여 테스트 대상 객체 생성

    Given("유저 저장시") {
        val request = UserSaveRequest(
            email = "test@email",
            nickname = "test_nickname",
            password = "test_password"
        )
        When("정상 저장하면") {
            every { userRepository.findByEmail(any()) } returns null
            every { userRepository.findByNickname(any()) } returns null
            every { userRepository.save(any()).id } returns mockk()
            userService.save(request)
            Then("유저가 저장된다") {
                verify(exactly = 1) { userRepository.save(any()) }
            }
        }
    }
})