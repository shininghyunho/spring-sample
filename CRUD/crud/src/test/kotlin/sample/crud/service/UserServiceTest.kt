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
import sample.crud.controller.dto.user.UserUpdateRequest
import sample.crud.entity.User
import sample.crud.repository.UserRepository

class UserServiceTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>(relaxed = true) {
        every { findByEmail(any()) } returns null
        every { findByNickname(any()) } returns null
    } // mock 객체 생성
    val userService = UserService(userRepository) // mock 객체를 주입하여 테스트 대상 객체 생성

    Given("유저 저장시") {
        val request = UserSaveRequest(
            email = "test@email",
            nickname = "test_nickname",
            password = "test_password"
        )
        When("정상 저장하면") {
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
            Then("이메일 중복 에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.save(request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_EMAIL
            }
        }
        When("닉네임 중복이면") {
            every { userRepository.findByNickname(any()) } returns mockk()
            Then("닉네임 중복 에러가 발생한다") {
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

    Given("유저 업데이트시") {
        val id = 1L
        val request = UserUpdateRequest(
            email = "test_email",
            nickname = "test_nickname",
            password = "test_password"
        )
        When("정상 업데이트하면") {
            val user = mockk<User>(relaxed = true)
            every { userService.getEntity(any()) } returns user
            userService.update(id, request)
            Then("유저가 업데이트된다") { verify(exactly = 1) { user.update(any(), any(), any()) } }
        }
        When("이메일 중복이면") {
            every { userRepository.findByEmail(any()) } returns mockk()
            Then("이메일 중복 에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.update(id, request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_EMAIL
            }
        }
        When("닉네임 중복이면") {
            every { userRepository.findByNickname(any()) } returns mockk()
            Then("닉네임 중복 에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.update(id, request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_NICKNAME
            }
        }
        When("유저가 없으면") {
            every { userService.getEntity(any()) } returns null
            Then("유저 없음 에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.update(id, request)
                }.errorCode shouldBe ErrorCode.NOT_EXISTED_USER
            }
        }
    }

    Given("유저 삭제시") {
        val id = 1L
        When("정상 삭제하면") {
            every { userService.getEntity(any()) } returns mockk()
            userService.delete(id)
            Then("유저가 삭제된다") { verify(exactly = 1) { userRepository.delete(any()) } }
        }
        When("유저가 없으면") {
            every { userService.getEntity(any()) } returns null
            Then("유저 없음 에러가 발생한다") {
                shouldThrow<CustomException> {
                    userService.delete(id)
                }.errorCode shouldBe ErrorCode.NOT_EXISTED_USER
            }
        }
    }

    // private method 테스트
    Given("랜덤 닉네임 생성시") {
        When("정상 호출하면") {
            val privateMethod= UserService::class.java.getDeclaredMethod("getRandomNickname")
            privateMethod.isAccessible = true
            every { userRepository.findByNickname(any()) } returns null

            val result = privateMethod.invoke(userService) as String
            println(result)
            val randomLength = 5
            Then("조합이 USER_랜덤(숫자+영어 대소문자) $randomLength 자리 형식이다") { result.matches(Regex("USER_[a-zA-Z0-9]{$randomLength}")) shouldBe true }
        }
    }
})