package sample.crud.test.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.entity.User
import sample.crud.repository.UserRepository
import sample.crud.support.IntegrationTest

@IntegrationTest
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userRepository: UserRepository,
) : BehaviorSpec() {
    val objectMapper = jacksonObjectMapper()

    init {
        Given("유저 저장시") {
            val request = UserSaveRequest(
                email = "email",
                nickname = "nickname",
                password = "password"
            )
            When("정상 저장하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("유저 id가 반환된다") {
                    val userId = userRepository.findAll().first().id
                    result shouldContain userId.toString()
                }
                Then("상태코드는 201이다.") { result shouldContain HttpStatus.CREATED.value().toString() }
            }
            When("email이 중복되면") {
                saveUser()
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("이메일 중복 에러가 발생한다.") { result shouldContain ErrorCode.DUPLICATED_EMAIL.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("닉네임이 중복된다면") {
                userRepository.save(User(
                    email = "email2",
                    nickname = "nickname",
                    password = "password"
                ))
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("닉네임 중복 에러가 발생한다.") { result shouldContain ErrorCode.DUPLICATED_NICKNAME.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
        Given("유저 조회시") {
            val user = saveUser()
            When("정상 조회하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/users/${user.id}")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("유저 정보가 반환된다") {
                    result shouldContain user.id.toString()
                    result shouldContain user.email
                    result shouldContain user.nickname
                }
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("유저가 없다면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("data는 null을 반환한다.") { result shouldContain "null" }
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
        }
        Given("유저 업데이트 할때") {
            val user = saveUser()
            When("정상 업데이트하면") {
                val request = UserSaveRequest(
                    email = "email2",
                    nickname = "nickname2",
                    password = "password2"
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/users/${user.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("유저 정보가 업데이트 된다") {
                    val updatedUser = userRepository.findById(user.id).orElseThrow { CustomException(ErrorCode.NOT_EXISTED_USER) }
                    updatedUser.email shouldBe request.email
                    updatedUser.nickname shouldBe request.nickname
                    updatedUser.password shouldBe request.password
                }
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("이메일이 중복되면") {
                userRepository.save(User(
                    email = "email2",
                    nickname = "nickname2",
                    password = "password2"
                ))
                val request = UserSaveRequest(
                    email = "email2",
                    nickname = "nickname2",
                    password = "password2"
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/users/${user.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("이메일 중복 에러가 발생한다.") { result shouldContain ErrorCode.DUPLICATED_EMAIL.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("닉네임이 중복된다면") {
                userRepository.save(User(
                        email = "email2",
                        nickname = "nickname2",
                        password = "password2"
                ))
                val request = UserSaveRequest(
                    email = "email3",
                    nickname = "nickname2",
                    password = "password2"
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/users/${user.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("닉네임 중복 에러가 발생한다.") { result shouldContain ErrorCode.DUPLICATED_NICKNAME.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
        Given("유저 삭제할때") {
            val user = saveUser()
            When("정상 삭제하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/users/${user.id}")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("유저 정보가 삭제된다") {
                    userRepository.findById(user.id).orElse(null) shouldBe null
                }
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("유저가 없다면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("유저 없음 에러가 발생한다.") { result shouldContain ErrorCode.NOT_EXISTED_USER.code }
                Then("상태코드는 400이다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
    }

    private fun saveUser() = userRepository.save(
        User (
            email = "email",
            nickname = "nickname",
            password = "password"
        )
    )
}