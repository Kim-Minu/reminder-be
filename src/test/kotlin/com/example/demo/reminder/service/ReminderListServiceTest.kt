package com.example.demo.reminder.service

import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.dto.CreateReminderListRequest
import com.example.demo.reminder.dto.UpdateReminderListRequest
import com.example.demo.reminder.repository.ReminderListRepository
import com.example.demo.reminder.service.ports.inp.ReminderListService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ReminderListServiceTest {

    @Autowired lateinit var service: ReminderListService
    @Autowired lateinit var repository: ReminderListRepository

    private val memberId = 1L
    private val otherMemberId = 2L

    private fun saveList(name: String, order: Int = 0, owner: Long = memberId) =
        repository.save(ReminderList(memberId = owner, name = name, displayOrder = order))

    @Nested
    inner class FindAll {

        @Test
        fun `자신의 목록만 displayOrder 오름차순으로 반환한다`() {
            saveList("직장", order = 1)
            saveList("집", order = 0)
            saveList("다른 멤버", owner = otherMemberId)

            val result = service.findAll(memberId)

            assertThat(result.map { it.name }).containsExactly("집", "직장")
        }

        @Test
        fun `저장된 목록이 없으면 빈 리스트를 반환한다`() {
            assertThat(service.findAll(memberId)).isEmpty()
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `자신의 목록을 id로 조회한다`() {
            val saved = saveList("집")

            val result = service.findById(saved.id, memberId)

            assertThat(result.name).isEqualTo("집")
        }

        @Test
        fun `다른 멤버의 목록을 조회하면 NoSuchElementException을 던진다`() {
            val saved = saveList("집", owner = otherMemberId)

            assertThatThrownBy { service.findById(saved.id, memberId) }
                .isInstanceOf(NoSuchElementException::class.java)
        }

        @Test
        fun `존재하지 않는 id로 조회하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { service.findById(999L, memberId) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `name과 color로 목록을 생성하면 DB에 저장된다`() {
            val result = service.create(memberId, CreateReminderListRequest(name = "장보기", color = "#FF3B30"))

            assertThat(result.id).isPositive()
            assertThat(result.name).isEqualTo("장보기")
            assertThat(result.color).isEqualTo("#FF3B30")
        }

        @Test
        fun `displayOrder는 해당 멤버의 기존 목록 수로 설정된다`() {
            service.create(memberId, CreateReminderListRequest("첫 번째"))
            service.create(memberId, CreateReminderListRequest("두 번째"))
            val third = service.create(memberId, CreateReminderListRequest("세 번째"))

            assertThat(third.displayOrder).isEqualTo(2)
        }

        @Test
        fun `다른 멤버의 목록 수는 displayOrder 계산에 영향을 주지 않는다`() {
            service.create(otherMemberId, CreateReminderListRequest("다른 멤버 목록"))
            service.create(otherMemberId, CreateReminderListRequest("다른 멤버 목록2"))

            val result = service.create(memberId, CreateReminderListRequest("나의 첫 번째"))

            assertThat(result.displayOrder).isEqualTo(0)
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `자신의 목록을 수정한다`() {
            val saved = saveList("이전")

            service.update(saved.id, memberId, UpdateReminderListRequest(name = "새 이름", color = "#FF9500", displayOrder = 2))

            val updated = repository.findById(saved.id).get()
            assertThat(updated.name).isEqualTo("새 이름")
            assertThat(updated.color).isEqualTo("#FF9500")
            assertThat(updated.displayOrder).isEqualTo(2)
        }

        @Test
        fun `다른 멤버의 목록을 수정하면 NoSuchElementException을 던진다`() {
            val saved = saveList("집", owner = otherMemberId)

            assertThatThrownBy {
                service.update(saved.id, memberId, UpdateReminderListRequest(name = "변경", color = "#000000", displayOrder = 0))
            }.isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `자신의 목록을 삭제하면 DB에서 제거된다`() {
            val saved = saveList("삭제 대상")

            service.delete(saved.id, memberId)

            assertThat(repository.existsById(saved.id)).isFalse()
        }

        @Test
        fun `다른 멤버의 목록을 삭제하면 NoSuchElementException을 던진다`() {
            val saved = saveList("집", owner = otherMemberId)

            assertThatThrownBy { service.delete(saved.id, memberId) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }
}
