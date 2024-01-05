package sample.crud.test.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import sample.crud.entity.Item
import sample.crud.entity.Order
import sample.crud.entity.OrderItem
import sample.crud.repository.OrderItemRepository
import sample.crud.service.OrderItemService

class OrderItemServiceTest : BehaviorSpec({
    val orderItemRepository = mockk<OrderItemRepository>(relaxed = true) // mock 객체 생성
    val orderItemService = OrderItemService(orderItemRepository)

    Given("주문 아이템 저장시") {
        val order = mockk<Order>() {
            every { orderItems } returns mutableSetOf()
        }
        val item = Item(name = "item", price = 1000, quantity = 10)
        When("정상 저장하면") {
            val beforeQuantity = item.quantity
            every { orderItemRepository.save(any()).id } returns 1L
            val count = 5
            val result = orderItemService.save(order, item, count)
            Then("주문 아이템이 저장된다") { verify (exactly = 1) { orderItemRepository.save(any()) } }
            Then("주문 아이템 id가 반환된다") { result shouldBe 1L }
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