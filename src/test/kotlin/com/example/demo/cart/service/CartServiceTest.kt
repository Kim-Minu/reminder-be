package com.example.demo.cart.service

import com.example.demo.cart.domain.CartWeek
import com.example.demo.cart.dto.CartItemRequest
import com.example.demo.cart.dto.CartWeekRequest
import com.example.demo.cart.repository.CartItemRepository
import com.example.demo.cart.repository.CartWeekRepository
import com.example.demo.cart.service.ports.inp.CartService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired lateinit var service: CartService
    @Autowired lateinit var cartWeekRepository: CartWeekRepository
    @Autowired lateinit var cartItemRepository: CartItemRepository
    @Autowired lateinit var em: EntityManager

    private val memberId = 1L
    private val otherMemberId = 2L

    private fun saveWeek(year: Int = 2026, month: Int = 4, weekOfMonth: Int = 1, owner: Long = memberId) =
        cartWeekRepository.save(
            CartWeek(memberId = owner, year = year, month = month, weekOfMonth = weekOfMonth, label = "${month}월 ${weekOfMonth}주차")
        )

    private fun itemRequest(name: String = "사과", quantity: Int = 2, unitPrice: Int = 1000) =
        CartItemRequest(name = name, quantity = quantity, unitPrice = unitPrice)

    @Nested
    inner class FindWeeksByMonth {

        @Test
        fun `해당 월의 주차 목록을 weekOfMonth 오름차순으로 반환한다`() {
            saveWeek(weekOfMonth = 2)
            saveWeek(weekOfMonth = 1)
            saveWeek(month = 5, weekOfMonth = 1) // 다른 달

            val result = service.findWeeksByMonth(memberId, 2026, 4)

            assertThat(result).hasSize(2)
            assertThat(result.map { it.weekOfMonth }).containsExactly(1, 2)
        }

        @Test
        fun `다른 멤버의 주차는 반환하지 않는다`() {
            saveWeek(owner = otherMemberId)

            val result = service.findWeeksByMonth(memberId, 2026, 4)

            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class FindOrCreateWeek {

        @Test
        fun `존재하지 않으면 새 주차를 생성한다`() {
            val result = service.findOrCreateWeek(memberId, CartWeekRequest(2026, 4, 1))

            assertThat(result.id).isPositive()
            assertThat(result.label).isEqualTo("4월 1주차")
        }

        @Test
        fun `이미 존재하면 기존 주차를 반환한다`() {
            val saved = saveWeek()

            val result = service.findOrCreateWeek(memberId, CartWeekRequest(2026, 4, 1))

            assertThat(result.id).isEqualTo(saved.id)
        }
    }

    @Nested
    inner class CreateItem {

        @Test
        fun `아이템을 추가하면 lineTotal과 displayOrder가 설정된다`() {
            val week = saveWeek()

            val result = service.createItem(week.id, memberId, itemRequest("사과", 2, 1000))

            assertThat(result.lineTotal).isEqualTo(2000)
            assertThat(result.displayOrder).isEqualTo(0)
        }

        @Test
        fun `아이템이 여러 개면 displayOrder가 순서대로 부여된다`() {
            val week = saveWeek()
            service.createItem(week.id, memberId, itemRequest("사과"))

            val second = service.createItem(week.id, memberId, itemRequest("바나나"))

            assertThat(second.displayOrder).isEqualTo(1)
        }

        @Test
        fun `다른 멤버의 주차에 아이템 추가 시 NoSuchElementException을 던진다`() {
            val week = saveWeek(owner = otherMemberId)

            assertThatThrownBy { service.createItem(week.id, memberId, itemRequest()) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class UpdateItem {

        @Test
        fun `아이템 이름, 수량, 단가를 수정한다`() {
            val week = saveWeek()
            val item = service.createItem(week.id, memberId, itemRequest("사과", 1, 500))

            val result = service.updateItem(item.id, memberId, itemRequest("포도", 3, 2000))

            assertThat(result.name).isEqualTo("포도")
            assertThat(result.lineTotal).isEqualTo(6000)
        }

        @Test
        fun `다른 멤버의 아이템 수정 시 NoSuchElementException을 던진다`() {
            val week = saveWeek(owner = otherMemberId)
            val item = service.createItem(week.id, otherMemberId, itemRequest())

            assertThatThrownBy { service.updateItem(item.id, memberId, itemRequest("변경")) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class ToggleCheck {

        @Test
        fun `체크 상태를 토글한다`() {
            val week = saveWeek()
            val item = service.createItem(week.id, memberId, itemRequest())

            assertThat(item.isChecked).isFalse()

            val toggled = service.toggleCheck(item.id, memberId)
            assertThat(toggled.isChecked).isTrue()
        }
    }

    @Nested
    inner class DeleteItem {

        @Test
        fun `아이템을 삭제하면 DB에서 제거된다`() {
            val week = saveWeek()
            val item = service.createItem(week.id, memberId, itemRequest())

            service.deleteItem(item.id, memberId)

            assertThat(cartItemRepository.existsById(item.id)).isFalse()
        }
    }

    @Nested
    inner class DeleteCheckedItems {

        @Test
        fun `체크된 아이템만 일괄 삭제한다`() {
            val week = saveWeek()
            val checked = service.createItem(week.id, memberId, itemRequest("사과"))
            val unchecked = service.createItem(week.id, memberId, itemRequest("바나나"))
            service.toggleCheck(checked.id, memberId)

            service.deleteCheckedItems(week.id, memberId)

            assertThat(cartItemRepository.existsById(checked.id)).isFalse()
            assertThat(cartItemRepository.existsById(unchecked.id)).isTrue()
        }
    }

    @Nested
    inner class WeekTotal {

        @Test
        fun `totalAmount는 전체 아이템 합산, checkedAmount는 체크 아이템만 합산한다`() {
            val week = saveWeek()
            val item1 = service.createItem(week.id, memberId, itemRequest("사과", 2, 1000))
            val item2 = service.createItem(week.id, memberId, itemRequest("바나나", 1, 500))
            service.toggleCheck(item1.id, memberId)

            em.flush()
            em.clear()

            val result = service.findWeeksByMonth(memberId, 2026, 4).first()

            assertThat(result.totalAmount).isEqualTo(2500)
            assertThat(result.checkedAmount).isEqualTo(2000)
        }
    }
}
