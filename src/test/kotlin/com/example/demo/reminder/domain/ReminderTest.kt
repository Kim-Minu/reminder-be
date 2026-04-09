package com.example.demo.reminder.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReminderTest {

    private fun makeList() = ReminderList(memberId = 1L, name = "테스트 목록", color = "#007AFF", displayOrder = 0)

    @Nested
    inner class Constructor {

        @Test
        fun `Reminder가 정상적으로 생성된다`() {
            val reminder = Reminder(reminderList = makeList(), title = "할 일")

            assertThat(reminder.title).isEqualTo("할 일")
            assertThat(reminder.isCompleted).isFalse()
            assertThat(reminder.displayOrder).isEqualTo(0)
        }

        @Test
        fun `displayOrder를 지정하면 해당 값으로 생성된다`() {
            val reminder = Reminder(reminderList = makeList(), title = "할 일", displayOrder = 3)

            assertThat(reminder.displayOrder).isEqualTo(3)
        }
    }

    @Nested
    inner class DateAutoRegistration {

        @Test
        fun `생성 시 createdAt과 updatedAt이 동일하다`() {
            val reminder = Reminder(reminderList = makeList(), title = "할 일")

            assertThat(reminder.createdAt).isEqualTo(reminder.updatedAt)
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `update 호출 시 title이 변경된다`() {
            val reminder = Reminder(reminderList = makeList(), title = "원래 제목")

            reminder.update("새 제목", null, false, Priority.NONE, null, null, 0)

            assertThat(reminder.title).isEqualTo("새 제목")
        }

        @Test
        fun `update 호출 시 notes가 변경된다`() {
            val reminder = Reminder(reminderList = makeList(), title = "할 일")

            reminder.update("할 일", "메모 추가", false, Priority.NONE, null, null, 0)

            assertThat(reminder.notes).isEqualTo("메모 추가")
        }

        @Test
        fun `update 호출 시 updatedAt이 갱신된다`() {
            val reminder = Reminder(reminderList = makeList(), title = "할 일")
            val before = reminder.updatedAt

            Thread.sleep(5)
            reminder.update("변경", null, false, Priority.NONE, null, null, 0)

            assertThat(reminder.updatedAt).isAfterOrEqualTo(before)
        }
    }
}
