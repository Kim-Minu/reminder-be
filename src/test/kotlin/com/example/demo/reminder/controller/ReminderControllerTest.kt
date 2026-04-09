package com.example.demo.reminder.controller

import com.example.demo.common.security.WithMockCustomUser
import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.repository.ReminderListRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@WithMockCustomUser
class ReminderControllerTest {

    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var reminderListRepository: ReminderListRepository

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper()
    private val memberId = 1L

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    private fun saveList(name: String = "테스트 목록") =
        reminderListRepository.save(ReminderList(memberId = memberId, name = name, color = "#007AFF", displayOrder = 0))

    @Nested
    inner class GetByListId {

        @Test
        fun `목록에 속한 리마인더를 displayOrder 순으로 반환한다`() {
            val list = saveList()
            mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "두 번째"))
            }
            mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "첫 번째"))
            }

            mockMvc.get("/api/reminder-lists/${list.id}/reminders")
                .andExpect {
                    status { isOk() }
                    jsonPath("$[0].title") { value("두 번째") }
                    jsonPath("$[1].title") { value("첫 번째") }
                }
        }

        @Test
        fun `리마인더가 없으면 빈 배열을 반환한다`() {
            val list = saveList()

            mockMvc.get("/api/reminder-lists/${list.id}/reminders")
                .andExpect {
                    status { isOk() }
                    jsonPath("$") { isEmpty() }
                }
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `유효한 요청으로 리마인더를 생성하면 201과 생성된 리마인더를 반환한다`() {
            val list = saveList()
            val body = mapOf("title" to "새 할 일")

            mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.title") { value("새 할 일") }
                jsonPath("$.listId") { value(list.id) }
                jsonPath("$.isCompleted") { value(false) }
                jsonPath("$.id") { isNumber() }
            }
        }

        @Test
        fun `title이 빈 문자열이면 400을 반환한다`() {
            val list = saveList()
            val body = mapOf("title" to "")

            mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `존재하지 않는 listId로 생성하면 404를 반환한다`() {
            val body = mapOf("title" to "제목")

            mockMvc.post("/api/reminder-lists/999/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `유효한 요청으로 리마인더를 수정하면 200과 수정된 리마인더를 반환한다`() {
            val list = saveList()
            val created = mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "원래 제목"))
            }.andReturn().response.let { objectMapper.readTree(it.contentAsString) }
            val id = created["id"].asLong()

            mockMvc.put("/api/reminder-lists/${list.id}/reminders/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "수정된 제목", "notes" to "메모"))
            }.andExpect {
                status { isOk() }
                jsonPath("$.title") { value("수정된 제목") }
                jsonPath("$.notes") { value("메모") }
            }
        }

        @Test
        fun `존재하지 않는 리마인더를 수정하면 404를 반환한다`() {
            val list = saveList()

            mockMvc.put("/api/reminder-lists/${list.id}/reminders/999") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "제목"))
            }.andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `리마인더를 삭제하면 204를 반환한다`() {
            val list = saveList()
            val created = mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "삭제할 항목"))
            }.andReturn().response.let { objectMapper.readTree(it.contentAsString) }
            val id = created["id"].asLong()

            mockMvc.delete("/api/reminder-lists/${list.id}/reminders/$id")
                .andExpect { status { isNoContent() } }
        }

        @Test
        fun `존재하지 않는 리마인더를 삭제하면 404를 반환한다`() {
            val list = saveList()

            mockMvc.delete("/api/reminder-lists/${list.id}/reminders/999")
                .andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class ToggleComplete {

        @Test
        fun `완료 토글 시 200과 변경된 리마인더를 반환한다`() {
            val list = saveList()
            val created = mockMvc.post("/api/reminder-lists/${list.id}/reminders") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("title" to "할 일"))
            }.andReturn().response.let { objectMapper.readTree(it.contentAsString) }
            val id = created["id"].asLong()

            mockMvc.patch("/api/reminder-lists/${list.id}/reminders/$id/complete")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.isCompleted") { value(true) }
                    jsonPath("$.completedAt") { isNotEmpty() }
                }
        }

        @Test
        fun `존재하지 않는 리마인더를 토글하면 404를 반환한다`() {
            val list = saveList()

            mockMvc.patch("/api/reminder-lists/${list.id}/reminders/999/complete")
                .andExpect { status { isNotFound() } }
        }
    }
}
