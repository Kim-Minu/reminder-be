package com.example.demo.reminder.controller

import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.repository.ReminderListRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@WithMockUser
class ReminderListControllerTest {

    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var repository: ReminderListRepository

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    @Nested
    inner class GetAll {

        @Test
        fun `저장된 목록을 displayOrder 순으로 반환한다`() {
            repository.save(ReminderList(name = "직장", displayOrder = 1))
            repository.save(ReminderList(name = "집", displayOrder = 0))

            mockMvc.get("/api/reminder-lists")
                .andExpect {
                    status { isOk() }
                    jsonPath("$[0].name") { value("집") }
                    jsonPath("$[1].name") { value("직장") }
                }
        }

        @Test
        fun `목록이 없으면 빈 배열을 반환한다`() {
            mockMvc.get("/api/reminder-lists")
                .andExpect {
                    status { isOk() }
                    jsonPath("$") { isEmpty() }
                }
        }
    }

    @Nested
    inner class GetById {

        @Test
        fun `존재하는 id로 조회하면 200과 목록을 반환한다`() {
            val saved = repository.save(ReminderList(name = "집", color = "#007AFF"))

            mockMvc.get("/api/reminder-lists/${saved.id}")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(saved.id) }
                    jsonPath("$.name") { value("집") }
                    jsonPath("$.color") { value("#007AFF") }
                }
        }

        @Test
        fun `존재하지 않는 id로 조회하면 404를 반환한다`() {
            mockMvc.get("/api/reminder-lists/999")
                .andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `유효한 요청으로 생성하면 201과 생성된 목록을 반환한다`() {
            val body = mapOf("name" to "장보기", "color" to "#FF3B30")

            mockMvc.post("/api/reminder-lists") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.name") { value("장보기") }
                jsonPath("$.color") { value("#FF3B30") }
                jsonPath("$.id") { isNumber() }
            }
        }

        @Test
        fun `color를 생략하면 기본값 #007AFF로 생성된다`() {
            val body = mapOf("name" to "목록")

            mockMvc.post("/api/reminder-lists") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.color") { value("#007AFF") }
            }
        }

        @Test
        fun `name이 빈 문자열이면 400을 반환한다`() {
            val body = mapOf("name" to "")

            mockMvc.post("/api/reminder-lists") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `color가 hex 형식이 아니면 400을 반환한다`() {
            val body = mapOf("name" to "목록", "color" to "red")

            mockMvc.post("/api/reminder-lists") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `유효한 요청으로 수정하면 200과 수정된 목록을 반환한다`() {
            val saved = repository.save(ReminderList(name = "이전", color = "#007AFF", displayOrder = 0))
            val body = mapOf("name" to "새 이름", "color" to "#FF9500", "displayOrder" to 1)

            mockMvc.put("/api/reminder-lists/${saved.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isOk() }
                jsonPath("$.name") { value("새 이름") }
                jsonPath("$.color") { value("#FF9500") }
                jsonPath("$.displayOrder") { value(1) }
            }
        }

        @Test
        fun `존재하지 않는 id로 수정하면 404를 반환한다`() {
            val body = mapOf("name" to "x", "color" to "#000000", "displayOrder" to 0)

            mockMvc.put("/api/reminder-lists/999") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `존재하는 id를 삭제하면 204를 반환한다`() {
            val saved = repository.save(ReminderList(name = "삭제 대상"))

            mockMvc.delete("/api/reminder-lists/${saved.id}")
                .andExpect { status { isNoContent() } }
        }

        @Test
        fun `존재하지 않는 id를 삭제하면 404를 반환한다`() {
            mockMvc.delete("/api/reminder-lists/999")
                .andExpect { status { isNotFound() } }
        }
    }
}
