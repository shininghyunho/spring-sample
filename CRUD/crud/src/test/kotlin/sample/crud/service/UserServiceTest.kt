package sample.crud.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.entity.User
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

            val savedUser = mockk<User>()
            val userId = 123L
            every { userRepository.save(any()) } returns savedUser
            every { savedUser.id } returns userId
            val result = userService.save(request)

            Then("유저가 저장된다") { verify(exactly = 1) { userRepository.save(any()) } }
            Then("유저 id가 반환된다") { result shouldBe userId }
        }
        When("이메일 중복이면") {
            every { userRepository.findByEmail(any()) } returns mockk()
            every { userRepository.findByNickname(any()) } returns null
            Then("에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.save(request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_EMAIL
            }
        }
        When("닉네임 중복이면") {
            every { userRepository.findByEmail(any()) } returns null
            every { userRepository.findByNickname(any()) } returns mockk()
            Then("에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.save(request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_NICKNAME
            }
        }
    }

    Given("유저 반환시") {
        val id = 1L
        When("정상 반환하면") {
            every { userRepository.findById(any()).orElse(null) } returns mockk()
            userService.getEntity(id)
            Then("유저가 반환된다") {
                verify(exactly = 1) { userRepository.findById(any()) }
            }
        }
        When("유저가 없으면") {
            every { userRepository.findById(any()).orElse(null) } returns null
            Then("null을 반환한다") {
                userService.getEntity(id) shouldBe null
            }
        }
    }
})