package com.example.demo.reminder.service

import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.dto.ReminderRequest
import com.example.demo.reminder.repository.ReminderListRepository
import com.example.demo.reminder.repository.ReminderRepository
import com.example.demo.reminder.service.ports.inp.ReminderService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ReminderServiceTest {

    @Autowired lateinit var service: ReminderService
    @Autowired lateinit var reminderListRepository: ReminderListRepository
    @Autowired lateinit var reminderRepository: ReminderRepository

    private val memberId = 1L

    private fun saveList(name: String = "테스트 목록") =
        reminderListRepository.save(ReminderList(memberId = memberId, name = name, color = "#007AFF", displayOrder = 0))

    @Nested
    inner class FindByListId {

        @Test
        fun `listId로 리마인더 목록을 displayOrder 순으로 반환한다`() {
            val list = saveList()
            service.create(list.id, memberId, ReminderRequest("두 번째"))
            service.create(list.id, memberId, ReminderRequest("첫 번째"))

            val result = service.findByListId(list.id, memberId)

            assertThat(result.map { it.title }).containsExactly("두 번째", "첫 번째")
        }

        @Test
        fun `리마인더가 없으면 빈 리스트를 반환한다`() {
            val list = saveList()

            val result = service.findByListId(list.id, memberId)

            assertThat(result).isEmpty()
        }

        @Test
        fun `다른 목록의 리마인더는 포함하지 않는다`() {
            val list1 = saveList("목록1")
            val list2 = saveList("목록2")
            service.create(list1.id, memberId, ReminderRequest("목록1 항목"))
            service.create(list2.id, memberId, ReminderRequest("목록2 항목"))

            val result = service.findByListId(list1.id, memberId)

            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("목록1 항목")
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `listId와 title로 리마인더를 생성하면 DB에 저장된다`() {
            val list = saveList()

            val result = service.create(list.id, memberId, ReminderRequest("새 리마인더"))

            assertThat(result.id).isPositive()
            assertThat(result.title).isEqualTo("새 리마인더")
            assertThat(result.listId).isEqualTo(list.id)
            assertThat(result.isCompleted).isFalse()
        }

        @Test
        fun `displayOrder는 기존 리마인더 수로 자동 설정된다`() {
            val list = saveList()
            service.create(list.id, memberId, ReminderRequest("첫 번째"))
            service.create(list.id, memberId, ReminderRequest("두 번째"))

            val third = service.create(list.id, memberId, ReminderRequest("세 번째"))

            assertThat(third.displayOrder).isEqualTo(2)
        }

        @Test
        fun `존재하지 않는 listId로 생성하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { service.create(999L, memberId, ReminderRequest("제목")) }
                .isInstanceOf(NoSuchElementException::class.java)
                .hasMessageContaining("999")
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `title과 notes를 수정하면 변경사항이 반영된다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("기존 제목"))

            val result = service.update(created.id, memberId, ReminderRequest(
                title = "수정된 제목",
                notes = "메모 추가",
            ))

            assertThat(result.title).isEqualTo("수정된 제목")
            assertThat(result.notes).isEqualTo("메모 추가")
        }

        @Test
        fun `다른 멤버의 리마인더를 수정하면 NoSuchElementException을 던진다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("제목"))

            assertThatThrownBy { service.update(created.id, 999L, ReminderRequest("수정")) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `리마인더를 삭제하면 DB에서 제거된다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("삭제할 항목"))

            service.delete(created.id, memberId)

            assertThat(reminderRepository.findById(created.id)).isEmpty()
        }

        @Test
        fun `다른 멤버의 리마인더를 삭제하면 NoSuchElementException을 던진다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("제목"))

            assertThatThrownBy { service.delete(created.id, 999L) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class ToggleComplete {

        @Test
        fun `완료 토글 시 isCompleted가 true로 변경되고 completedAt이 설정된다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("할 일"))

            val result = service.toggleComplete(created.id, memberId)

            assertThat(result.isCompleted).isTrue()
            assertThat(result.completedAt).isNotNull()
        }

        @Test
        fun `완료 상태에서 토글하면 isCompleted가 false로 변경되고 completedAt이 null이 된다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("할 일"))
            service.toggleComplete(created.id, memberId)

            val result = service.toggleComplete(created.id, memberId)

            assertThat(result.isCompleted).isFalse()
            assertThat(result.completedAt).isNull()
        }

        @Test
        fun `다른 멤버의 리마인더를 토글하면 NoSuchElementException을 던진다`() {
            val list = saveList()
            val created = service.create(list.id, memberId, ReminderRequest("할 일"))

            assertThatThrownBy { service.toggleComplete(created.id, 999L) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }
}
