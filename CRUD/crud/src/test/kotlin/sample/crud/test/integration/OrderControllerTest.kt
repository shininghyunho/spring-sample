package sample.crud.test.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import sample.crud.common.response.ErrorCode
import sample.crud.controller.dto.order.OrderSaveRequest
import sample.crud.entity.Item
import sample.crud.entity.Order
import sample.crud.entity.OrderItem
import sample.crud.entity.User
import sample.crud.repository.ItemRepository
import sample.crud.repository.OrderItemRepository
import sample.crud.repository.OrderRepository
import sample.crud.repository.UserRepository
import sample.crud.support.IntegrationTest

@IntegrationTest
class OrderControllerTest (
    @Autowired val mockMvc: MockMvc,
    @Autowired val userRepository: UserRepository,
    @Autowired val itemRepository: ItemRepository,
    @Autowired val orderRepository: OrderRepository,
    @Autowired val orderItemRepository: OrderItemRepository,
) : BehaviorSpec() {
    val objectMapper = jacksonObjectMapper()

    init {
        Given("주문 저장시") {
            val user = saveUser()
            val items = listOf(
                saveItem("item1"),
                saveItem("item2"),
                saveItem("item3"),
            )
            val orderItemCount = 5;
            When("정상 저장하면") {
                val request = OrderSaveRequest(
                    userId = user.id,
                    items = items.map { OrderSaveRequest.Item(id = it.id, count = orderItemCount) }
                )
                val itemQuantityMap = items.associate { it.id to it.quantity }
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/orders")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("주문 id가 반환된다.") {
                    val orderId = orderRepository.findAll().first().id
                    result shouldContain orderId.toString()
                }
                Then("주문이 정상 저장된다.") {
                    val order = orderRepository.findAll().first()
                    order.user shouldBe user
                    order.orderItems.size shouldBe items.size
                    order.orderItems.forEach {
                        it.order shouldBe order
                        it.item shouldBeIn items
                    }
                }
                Then("주문 상품의 재고가 감소한다.") {
                    items.forEach {
                        val item = itemRepository.findById(it.id).get()
                        item.quantity shouldBe itemQuantityMap[it.id]!! - orderItemCount
                    }
                }
                Then("상태 코드 201이 반환된다.") { result shouldContain HttpStatus.CREATED.value().toString() }
            }
            When("유저 id가 존재하지 않으면") {
                val request = OrderSaveRequest(
                    userId = 0,
                    items = items.map { OrderSaveRequest.Item(id = it.id, count = orderItemCount) }
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/orders")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("존재하지 않는 유저 에러가 발생한다.") { result shouldContain ErrorCode.NOT_EXISTED_USER.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("상품 id가 존재하지 않으면") {
                val request = OrderSaveRequest(
                    userId = user.id,
                    items = listOf(OrderSaveRequest.Item(id = 0, count = orderItemCount))
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/orders")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("존재하지 않는 상품 에러가 발생한다.") { result shouldContain ErrorCode.NOT_EXISTED_ITEM.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
            When("상품의 재고가 부족하면") {
                val request = OrderSaveRequest(
                    userId = user.id,
                    items = items.map { OrderSaveRequest.Item(id = it.id, count = it.quantity + 1) }
                )
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/orders")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("재고 부족 에러가 발생한다.") { result shouldContain ErrorCode.NOT_ENOUGH_ITEM_QUANTITY.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
        Given("주문 조회시") {
            val user = saveUser()
            val items = listOf(
                saveItem("item1"),
                saveItem("item2"),
                saveItem("item3"),
            )
            val order = saveOrder(user, items)
            When("정상 조회하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/orders/${order.id}")
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("주문 정보가 반환된다.") {
                    result shouldContain order.id.toString()
                    result shouldContain user.id.toString()
                    items.forEach {
                        result shouldContain it.id.toString()
                        result shouldContain it.name
                        result shouldContain it.price.toString()
                    }
                }
                Then("상태 코드 200이 반환된다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("존재하지 않는 주문이면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/orders/0")
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("null을 반환한다.") { result shouldContain  "null" }
                Then("상태 코드 200이 반환된다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
        }
        Given("주문을 삭제할때") {
            val user = saveUser()
            val items = listOf(
                saveItem("item1"),
                saveItem("item2"),
                saveItem("item3"),
            )
            val order = saveOrder(user, items)
            When("정상 삭제하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/orders/${order.id}")
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("주문이 삭제된다.") {
                    orderRepository.findById(order.id).orElse(null) shouldBe null
                    orderItemRepository.findAll().forEach {
                        it.order shouldBe null
                        it.item shouldBeIn items
                    }
                }
                Then("상태 코드 200이 반환된다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("존재하지 않는 주문이면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/orders/0")
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("존재하지 않는 주문 에러가 발생한다.") { result shouldContain ErrorCode.NOT_EXISTED_ORDER.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
        Given("주문을 취소할때") {
            When("정상 취소되면") {
                val user = saveUser()
                val items = listOf(
                    saveItem("item1"),
                    saveItem("item2"),
                    saveItem("item3"),
                )
                val itemQuantityMap = items.associate { it.id to it.quantity }
                val orderItemCount = 3;
                val order = saveOrder(user, items, orderItemCount)

                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/orders/${order.id}/cancel")
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("주문이 취소된다.") {
                    orderRepository.findById(order.id).orElse(null) shouldBe null
                    orderItemRepository.findAll().forEach {
                        it.order shouldBe null
                        it.item shouldBeIn items
                    }
                }
                Then("주문 상품의 재고가 증가한다.") {
                    items.forEach {
                        val item = itemRepository.findById(it.id).get()
                        item.quantity shouldBe itemQuantityMap[it.id]!! + orderItemCount
                    }
                }
                Then("상태 코드 200이 반환된다.") { result shouldContain HttpStatus.OK.value().toString() }
            }
            When("존재하지 않는 주문이면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/orders/0/cancel")
                        .contentType("application/json"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().response.contentAsString
                Then("존재하지 않는 주문 에러가 발생한다.") { result shouldContain ErrorCode.NOT_EXISTED_ORDER.code }
                Then("상태 코드 400이 반환된다.") { result shouldContain HttpStatus.BAD_REQUEST.value().toString() }
            }
        }
    }

    private fun saveOrder(user:User,items:List<Item>,count:Int = 1) : Order {
        val order = orderRepository.save(Order(user = user))
        items.forEach {
            orderItemRepository.save(OrderItem(order = order, item = it, count = count))
        }
        return order
    }
    private fun saveUser() = userRepository.save(User(
        email = "email",
        nickname = "nickname",
        password = "password",
    ))

    private fun saveItem(name: String) = itemRepository.save(Item(
        name = name,
        price = 1000,
        quantity = 10,
    ))
}