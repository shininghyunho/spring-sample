package sample.crud.test.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.controller.dto.order.OrderSaveRequest
import sample.crud.repository.OrderRepository
import sample.crud.service.ItemService
import sample.crud.service.OrderItemService
import sample.crud.service.OrderService
import sample.crud.service.UserService

class OrderServiceTest : BehaviorSpec({
    val orderRepository = mockk<OrderRepository>(relaxed = true)
    val userService = mockk<UserService>(relaxed = true)
    val itemService = mockk<ItemService>(relaxed = true)
    val orderItemService = mockk<OrderItemService>(relaxed = true)
    val orderService = OrderService(orderRepository, userService, itemService, orderItemService)

    Given("주문 저장시") {
        val request = OrderSaveRequest(
            userId = 1L,
            items = listOf(
                OrderSaveRequest.Item(
                    id = 1L,
                    count = 1
                ),
                OrderSaveRequest.Item(
                    id = 2L,
                    count = 1
                )
            )
        )
        every { userService.getEntity(any()) } returns mockk {
            every { orders } returns mutableSetOf()
        }
        every { orderRepository.save(any()) } returns mockk {
            every { id } returns 0L
        }
        When("정상 저장하면") {
            val result = orderService.save(request)
            Then("주문이 저장된다") { verify(exactly = 1) { orderRepository.save(any()) } }
            Then("주문 id가 반환된다") { result shouldBe 0L }

        }
        When("유저 id에 해당하는 유저가 없다면") {
            every { userService.getEntity(any()) } returns null
            Then("유저 없음 에러가 발생한다") {
                shouldThrow<CustomException> {
                    orderService.save(request)
                }.errorCode shouldBe ErrorCode.NOT_EXISTED_USER
            }
        }
        When("아이템 id에 해당하는 아이템이 없다면") {
            every { itemService.getEntity(any()) } returns null
            Then("아이템 없음 에러가 발생한다") {
                shouldThrow<CustomException> {
                    orderService.save(request)
                }.errorCode shouldBe ErrorCode.NOT_EXISTED_ITEM
            }
        }
    }
})