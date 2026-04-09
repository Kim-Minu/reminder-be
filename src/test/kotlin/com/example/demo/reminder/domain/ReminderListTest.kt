package com.example.demo.reminder.domain

import com.example.demo.reminder.dto.UpdateReminderListRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ReminderListTest {

    private val memberId = 1L

    // ──────────────────────────────────────────────
    // 생성자 테스트
    // ──────────────────────────────────────────────
    @Nested
    inner class Constructor {

        @Test
        fun `name만 지정하면 나머지는 기본값으로 설정된다`() {
            val list = ReminderList(memberId = memberId, name = "장보기")

            assertThat(list.name).isEqualTo("장보기")
            assertThat(list.color).isEqualTo("#007AFF")
            assertThat(list.displayOrder).isEqualTo(0)
            assertThat(list.reminders).isEmpty()
        }

        @Test
        fun `color와 displayOrder를 직접 지정하면 해당 값이 설정된다`() {
            val list = ReminderList(memberId = memberId, name = "직장", color = "#FF3B30", displayOrder = 2)

            assertThat(list.color).isEqualTo("#FF3B30")
            assertThat(list.displayOrder).isEqualTo(2)
        }

        @Test
        fun `생성 시 id 기본값은 0이다`() {
            val list = ReminderList(memberId = memberId, name = "집")

            assertThat(list.id).isEqualTo(0L)
        }
    }

    // ──────────────────────────────────────────────
    // 날짜 자동 등록 테스트
    // ──────────────────────────────────────────────
    @Nested
    inner class DateAutoRegistration {

        @Test
        fun `생성 시 createdAt이 현재 시각으로 설정된다`() {
            val before = LocalDateTime.now()
            val list = ReminderList(memberId = memberId, name = "테스트")
            val after = LocalDateTime.now()

            assertThat(list.createdAt).isAfterOrEqualTo(before)
            assertThat(list.createdAt).isBeforeOrEqualTo(after)
        }

        @Test
        fun `생성 시 updatedAt이 현재 시각으로 설정된다`() {
            val before = LocalDateTime.now()
            val list = ReminderList(memberId = memberId, name = "테스트")
            val after = LocalDateTime.now()

            assertThat(list.updatedAt).isAfterOrEqualTo(before)
            assertThat(list.updatedAt).isBeforeOrEqualTo(after)
        }

        @Test
        fun `생성 직후 createdAt과 updatedAt은 같다`() {
            val list = ReminderList(memberId = memberId, name = "테스트")

            assertThat(list.createdAt).isEqualTo(list.updatedAt)
        }
    }

    // ──────────────────────────────────────────────
    // update() 테스트
    // ──────────────────────────────────────────────
    @Nested
    inner class Update {

        @Test
        fun `update 호출 시 name이 변경된다`() {
            val list = ReminderList(memberId = memberId, name = "이전 이름")

            list.update(UpdateReminderListRequest(name = "새 이름", color = list.color, displayOrder = list.displayOrder))

            assertThat(list.name).isEqualTo("새 이름")
        }

        @Test
        fun `update 호출 시 color가 변경된다`() {
            val list = ReminderList(memberId = memberId, name = "목록", color = "#007AFF")

            list.update(UpdateReminderListRequest(name = list.name, color = "#FF9500", displayOrder = list.displayOrder))

            assertThat(list.color).isEqualTo("#FF9500")
        }

        @Test
        fun `update 호출 시 displayOrder가 변경된다`() {
            val list = ReminderList(memberId = memberId, name = "목록", displayOrder = 0)

            list.update(UpdateReminderListRequest(name = list.name, color = list.color, displayOrder = 5))

            assertThat(list.displayOrder).isEqualTo(5)
        }

        @Test
        fun `update 호출 시 updatedAt이 갱신된다`() {
            val list = ReminderList(memberId = memberId, name = "목록")
            val beforeUpdate = list.updatedAt

            Thread.sleep(10)
            list.update(UpdateReminderListRequest(name = "변경", color = list.color, displayOrder = list.displayOrder))

            assertThat(list.updatedAt).isAfter(beforeUpdate)
        }

        @Test
        fun `update 호출 후 createdAt은 변하지 않는다`() {
            val list = ReminderList(memberId = memberId, name = "목록")
            val originalCreatedAt = list.createdAt

            Thread.sleep(10)
            list.update(UpdateReminderListRequest(name = "변경", color = list.color, displayOrder = list.displayOrder))

            assertThat(list.createdAt).isEqualTo(originalCreatedAt)
        }
    }
}
