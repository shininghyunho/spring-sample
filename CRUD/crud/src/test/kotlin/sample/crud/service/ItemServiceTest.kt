package sample.crud.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.mockito.ArgumentMatchers.any
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.controller.dto.Item.ItemSaveRequest
import sample.crud.controller.dto.Item.ItemUpdateRequest
import sample.crud.entity.Item
import sample.crud.repository.ItemRepository

class ItemServiceTest : BehaviorSpec({
    val itemRepository = mockk<ItemRepository>(relaxed = true) // mock 객체 생성
    val itemService = ItemService(itemRepository)

    Given("아이템 저장시") {
        val request = ItemSaveRequest(
            name = "test_name",
            price = 1000L,
            quantity = 10,
        )
        When("정상 저장하면") {
            val savedItem = mockk<Item>()
            val itemId = 123L
            every { itemRepository.save(any()) } returns savedItem
            every { savedItem.id } returns itemId
            val result = itemService.save(request)

            Then("아이템이 저장된다") { verify(exactly = 1) { itemRepository.save(any()) } }
            Then("아이템 id가 반환된다") { result shouldBe itemId }
        }
        When("이름이 중복되면") {
            every {itemRepository.existsByName(any())} returns true
            Then("이름 중복 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.save(request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_ITEM_NAME
            }
        }
        When("아이템 가격이 음수이면") {
            val invalidRequest = ItemSaveRequest(
                name = "test_name",
                price = -1000L,
                quantity = 10,
            )
            Then("가격 범위 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.save(invalidRequest)
                }.errorCode shouldBe ErrorCode.INVALID_PRICE
            }
        }
        When("아이템 수량이 음수이면") {
            val invalidRequest = ItemSaveRequest(
                name = "test_name",
                price = 1000L,
                quantity = -10,
            )
            Then("수량 범위 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.save(invalidRequest)
                }.errorCode shouldBe ErrorCode.INVALID_QUANTITY
            }
        }
    }

    Given("아이템 반환시") {
        When("정상 반환하면") {
            val itemId = 123L
            val item = mockk<Item>(relaxed = true)
            every { itemRepository.findById(any()).orElse(null) } returns item
            val result = itemService.get(itemId)

            Then("아이템이 반환된다") { verify(exactly = 1) { itemRepository.findById(any()) } }
            Then("반환값은 null이 아니다") { result shouldNotBe null }
        }
        When("아이템이 없으면") {
            every { itemRepository.findById(any()).orElse(null) } returns null
            Then("null을 반환한다") {
                itemService.get(123L) shouldBe null
            }
        }
    }

    Given("아이템 수정시") {
        val request = ItemUpdateRequest(
            name = "update_name",
            price = 1100L,
            quantity = 11,
        )
        When("정상 수정하면") {
            val item = mockk<Item>(relaxed = true)
            every { itemRepository.findById(any()).orElse(null) } returns item
            itemService.update(123L, request)
            Then("아이템이 수정된다") {
                verify(exactly = 1) { itemRepository.findById(any()) }
            }
        }
        When("아이템 id가 존재하지 않는다면") {
            every { itemRepository.findById(any()).orElse(null) } returns null
            Then("아이템이 없다는 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.update(123L, request)
                }.errorCode shouldBe ErrorCode.NOT_EXISTED_ITEM
            }
        }
        When("아이템 이름이 중복된다면") {
            every { itemRepository.existsByName(any()) } returns true
            Then("아이템 이름 중복 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.update(123L, request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_ITEM_NAME
            }
        }
        When("아이템 가격이 음수이면") {
            val invalidRequest = ItemUpdateRequest(
                name = "update_name",
                price = -1100L,
                quantity = 11,
            )
            Then("가격 범위 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.update(123L, invalidRequest)
                }.errorCode shouldBe ErrorCode.INVALID_PRICE
            }
        }
        When("아이템 수량이 음수이면") {
            val invalidRequest = ItemUpdateRequest(
                name = "update_name",
                price = 1100L,
                quantity = -11,
            )
            Then("수량 범위 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.update(123L, invalidRequest)
                }.errorCode shouldBe ErrorCode.INVALID_QUANTITY
            }
        }
    }

    Given("아이템 삭제시") {
        When("정상 삭제하면") {
            val item = mockk<Item>(relaxed = true)
            every { itemRepository.findById(any()).orElse(null) } returns item
            itemService.delete(123L)
            Then("아이템이 삭제된다") {
                verify(exactly = 1) { itemRepository.delete(any()) }
            }
        }
        When("아이템 id가 존재하지 않는다면") {
            every { itemRepository.findById(any()).orElse(null) } returns null
            Then("아이템이 없다는 에러가 발생한다") {
                shouldThrow<CustomException> {
                    itemService.delete(123L)
                }.errorCode shouldBe ErrorCode.NOT_EXISTED_ITEM
            }
        }
    }
})