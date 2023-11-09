package sample.crud.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.entity.Item
import sample.crud.entity.Order
import sample.crud.entity.OrderItem
import sample.crud.repository.OrderItemRepository

class OrderItemServiceTest : BehaviorSpec({
    val orderItemRepository = mockk<OrderItemRepository>(relaxed = true) // mock 객체 생성
    val orderItemService = OrderItemService(orderItemRepository)

    Given("주문 아이템 저장시") {
        val order = mockk<Order>() {
            every { orderItems } returns mutableSetOf()
        }
        val item = mockk<Item> {
            every { quantity } returns 10
        }
        When("정상 저장하면") {
            every { orderItemRepository.save(any()).id } returns 1L
            val count = 5
            val result = orderItemService.save(order, item, count)
            Then("주문 아이템이 저장된다") { verify (exactly = 1) { orderItemRepository.save(any()) } }
            Then("주문 아이템 id가 반환된다") { result shouldBe 1L }
        }
        When("재고보다 많은 수량을 주문하면") {
            val count = 11
            Then("재고 부족 에러가 발생한다") {
                shouldThrow<CustomException> {
                    orderItemService.save(order, item, count)
                }.errorCode shouldBe ErrorCode.NOT_ENOUGH_ITEM_QUANTITY
            }
        }
    }

    Given("주문 아이템 엔티티 반환시") {
        val id = 1L
        When("정상 반환하면") {
            val orderItem = mockk<OrderItem>()
            every { orderItemRepository.findById(id) } returns mockk {
                every { orElse(null) } returns orderItem
            }
            val result = orderItemService.getEntity(id)
            Then("주문 아이템 엔티티가 반환된다") { result shouldBe orderItem }
        }
        When("없는 주문 아이템 id를 조회하면") {
            every { orderItemRepository.findById(id).orElse(null) } returns null
            Then("null이 반환된다") { orderItemService.getEntity(id) shouldBe null }
        }
    }
})