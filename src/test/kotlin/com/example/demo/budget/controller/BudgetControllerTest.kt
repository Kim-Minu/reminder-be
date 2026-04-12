package com.example.demo.budget.controller

import com.example.demo.budget.dto.SetMonthlyBudgetRequest
import com.example.demo.budget.dto.SetYearlyBudgetRequest
import com.example.demo.budget.service.ports.inp.BudgetService
import com.example.demo.common.security.WithMockCustomUser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@WithMockCustomUser
class BudgetControllerTest {

    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var budgetService: BudgetService

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper()
    private val memberId = 1L
    private val year = 2026

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    @Nested
    inner class GetByYear {

        @Test
        fun `연간 예산을 조회하면 200과 12개월 데이터를 반환한다`() {
            budgetService.setYearly(memberId, SetYearlyBudgetRequest(year, 500000))

            mockMvc.get("/api/budgets?year=$year")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.year") { value(year) }
                    jsonPath("$.yearlyAmount") { value(500000) }
                    jsonPath("$.months.length()") { value(12) }
                }
        }

        @Test
        fun `예산 미설정 시 yearlyAmount가 null이다`() {
            mockMvc.get("/api/budgets?year=$year")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.yearlyAmount") { value(null as Any?) }
                }
        }
    }

    @Nested
    inner class SetYearly {

        @Test
        fun `연간 예산 설정 시 200과 갱신된 연간 데이터를 반환한다`() {
            val body = mapOf("year" to year, "amount" to 500000)

            mockMvc.put("/api/budgets/yearly") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isOk() }
                jsonPath("$.yearlyAmount") { value(500000) }
                jsonPath("$.months.length()") { value(12) }
            }
        }

        @Test
        fun `amount가 음수이면 400을 반환한다`() {
            val body = mapOf("year" to year, "amount" to -1)

            mockMvc.put("/api/budgets/yearly") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class SetMonthly {

        @Test
        fun `월별 예산 설정 시 200과 갱신된 연간 데이터를 반환한다`() {
            val body = mapOf("year" to year, "month" to 4, "amount" to 300000)

            mockMvc.put("/api/budgets/monthly") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isOk() }
                jsonPath("$.months[3].month") { value(4) }
                jsonPath("$.months[3].amount") { value(300000) }
            }
        }

        @Test
        fun `month가 범위를 벗어나면 400을 반환한다`() {
            val body = mapOf("year" to year, "month" to 13, "amount" to 300000)

            mockMvc.put("/api/budgets/monthly") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }
    }
}
