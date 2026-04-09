package com.example.demo.reminder.service

import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.repository.ReminderListRepository
import com.example.demo.reminder.repository.ReminderRepository
import com.example.demo.reminder.service.ports.inp.ReminderListService
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
    @Autowired lateinit var reminderListService: ReminderListService
    @Autowired lateinit var reminderListRepository: ReminderListRepository
    @Autowired lateinit var reminderRepository: ReminderRepository

    private fun saveList(name: String = "테스트 목록") =
        reminderListRepository.save(ReminderList(name = name, color = "#007AFF", displayOrder = 0))

    @Nested
    inner class FindByListId {

        @Test
        fun `listId로 리마인더 목록을 displayOrder 순으로 반환한다`() {
            val list = saveList()
            service.create(list.id, "두 번째")
            service.create(list.id, "첫 번째")

            val result = service.findByListId(list.id)

            assertThat(result.map { it.title }).containsExactly("두 번째", "첫 번째")
        }

        @Test
        fun `리마인더가 없으면 빈 리스트를 반환한다`() {
            val list = saveList()

            val result = service.findByListId(list.id)

            assertThat(result).isEmpty()
        }

        @Test
        fun `다른 목록의 리마인더는 포함하지 않는다`() {
            val list1 = saveList("목록1")
            val list2 = saveList("목록2")
            service.create(list1.id, "목록1 항목")
            service.create(list2.id, "목록2 항목")

            val result = service.findByListId(list1.id)

            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("목록1 항목")
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `listId와 title로 리마인더를 생성하면 DB에 저장된다`() {
            val list = saveList()

            val result = service.create(list.id, "새 리마인더")

            assertThat(result.id).isPositive()
            assertThat(result.title).isEqualTo("새 리마인더")
            assertThat(result.reminderList.id).isEqualTo(list.id)
            assertThat(result.isCompleted).isFalse()
        }

        @Test
        fun `displayOrder는 기존 리마인더 수로 자동 설정된다`() {
            val list = saveList()
            service.create(list.id, "첫 번째")
            service.create(list.id, "두 번째")

            val third = service.create(list.id, "세 번째")

            assertThat(third.displayOrder).isEqualTo(2)
        }

        @Test
        fun `존재하지 않는 listId로 생성하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { service.create(999L, "제목") }
                .isInstanceOf(NoSuchElementException::class.java)
                .hasMessageContaining("999")
        }
    }
}
