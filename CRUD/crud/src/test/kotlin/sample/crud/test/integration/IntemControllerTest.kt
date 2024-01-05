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
import sample.crud.controller.dto.Item.ItemSaveRequest
import sample.crud.entity.Item
import sample.crud.repository.ItemRepository
import sample.crud.support.IntegrationTest

@IntegrationTest
class IntemControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val itemRepository: ItemRepository
) : BehaviorSpec() {
    val objectMapper = jacksonObjectMapper()

    init {
        Given("아이템 저장시") {
            When("정상 저장하면") {
                val request = ItemSaveRequest(
                    name = "name",
                    price = 1000,
                    quantity = 10
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 id가 반환된다") {
                    val itemId = itemRepository.findAll().first().id
                    result shouldContain itemId.toString()
                }
                Then("상태코드는 201이다.") { result shouldContain HttpStatus.CREATED.value().toString() }
            }
            When("아이템 이름이 중복된다면") {
                val request = ItemSaveRequest(
                    name = "name",
                    price = 1000,
                    quantity = 10
                )
                saveItem()
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 이름 중복 에러가 발생한다.") { result shouldContain ErrorCode.DUPLICATED_ITEM_NAME.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("아이템 가격이 음수면") {
                val request = ItemSaveRequest(
                    name = "name",
                    price = -1,
                    quantity = 10
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 가격 에러가 발생한다.") { result shouldContain ErrorCode.INVALID_PRICE.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("아이템 수량이 음수면") {
                val request = ItemSaveRequest(
                    name = "name",
                    price = 1000,
                    quantity = -1
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 수량 에러가 발생한다.") { result shouldContain ErrorCode.INVALID_QUANTITY.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
        Given("아이템 조회시") {
            val item = saveItem()
            When("정상 조회하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/items/${item.id}")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 정보가 반환된다") {
                    result shouldContain item.id.toString()
                    result shouldContain item.name
                    result shouldContain item.price.toString()
                    result shouldContain item.quantity.toString()
                }
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("존재하지 않는 아이템이라면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/items/999")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("null을 반환한다.") { result shouldContain "null" }
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
        }
        Given("아이템 업데이트시") {
            val item = saveItem()
            When("정상 업데이트하면") {
                val request = ItemSaveRequest(
                    name = "name2",
                    price = 2000,
                    quantity = 20
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/items/${item.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("아이템 이름이 중복된다면") {
                val request = ItemSaveRequest(
                    name = "name",
                    price = 2000,
                    quantity = 20
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/items/${item.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 이름 중복 에러가 발생한다.") { result shouldContain ErrorCode.DUPLICATED_ITEM_NAME.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("아이템 가격이 음수면") {
                val request = ItemSaveRequest(
                    name = "name2",
                    price = -1,
                    quantity = 20
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/items/${item.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 가격 에러가 발생한다.") { result shouldContain ErrorCode.INVALID_PRICE.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("아이템 수량이 음수면") {
                val request = ItemSaveRequest(
                    name = "name2",
                    price = 2000,
                    quantity = -1
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/items/${item.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("아이템 수량 에러가 발생한다.") { result shouldContain ErrorCode.INVALID_QUANTITY.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
        Given("아이템 삭제시") {
            val item = saveItem()
            When("정상 삭제하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/items/${item.id}")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("상태코드는 200이다.") { result shouldContain HttpStatus.OK.value().toString() }
                Then("아이템 정보가 삭제된다") { itemRepository.findById(item.id).orElse(null) shouldBe null }
            }
            When("존재하지 않는 아이템이라면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/items/999")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("존재하지 않는 아이템 에러가 발생한다.") { result shouldContain ErrorCode.NOT_EXISTED_ITEM.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
    }
    private fun saveItem() = itemRepository.save(Item(
            name = "name",
            price = 1000,
            quantity = 10,
        )
    )
}
