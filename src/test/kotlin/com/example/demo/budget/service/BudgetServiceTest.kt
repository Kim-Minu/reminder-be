package com.example.demo.budget.service

import com.example.demo.budget.dto.SetMonthlyBudgetRequest
import com.example.demo.budget.dto.SetYearlyBudgetRequest
import com.example.demo.budget.repository.BudgetRepository
import com.example.demo.budget.service.ports.inp.BudgetService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class BudgetServiceTest {

    @Autowired lateinit var service: BudgetService
    @Autowired lateinit var budgetRepository: BudgetRepository

    private val memberId = 1L
    private val year = 2026

    @Nested
    inner class GetByYear {

        @Test
        fun `연간 예산 미설정 시 yearlyAmount가 null이고 12개월이 반환된다`() {
            val result = service.getByYear(memberId, year)

            assertThat(result.yearlyAmount).isNull()
            assertThat(result.months).hasSize(12)
            assertThat(result.months.all { it.amount == null }).isTrue()
        }

        @Test
        fun `연간 예산 설정 후 yearlyAmount가 반영된다`() {
            service.setYearly(memberId, SetYearlyBudgetRequest(year, 500000))

            val result = service.getByYear(memberId, year)

            assertThat(result.yearlyAmount).isEqualTo(500000)
        }

        @Test
        fun `월별 개별 설정 시 해당 월의 amount가 반영된다`() {
            service.setYearly(memberId, SetYearlyBudgetRequest(year, 500000))
            service.setMonthly(memberId, SetMonthlyBudgetRequest(year, 4, 300000))

            val result = service.getByYear(memberId, year)

            assertThat(result.months.find { it.month == 4 }?.amount).isEqualTo(300000)
            assertThat(result.months.find { it.month == 5 }?.amount).isNull()
        }

        @Test
        fun `cartTotal은 체크된 항목 합산이며 초기에는 0이다`() {
            val result = service.getByYear(memberId, year)

            assertThat(result.months.all { it.cartTotal == 0 }).isTrue()
        }
    }

    @Nested
    inner class SetYearly {

        @Test
        fun `연간 예산을 처음 설정하면 저장된다`() {
            service.setYearly(memberId, SetYearlyBudgetRequest(year, 500000))

            val budget = budgetRepository.findYearlyBudget(memberId, year)
            assertThat(budget).isNotNull
            assertThat(budget!!.amount).isEqualTo(500000)
            assertThat(budget.month).isNull()
        }

        @Test
        fun `연간 예산을 재설정하면 덮어쓴다`() {
            service.setYearly(memberId, SetYearlyBudgetRequest(year, 500000))
            service.setYearly(memberId, SetYearlyBudgetRequest(year, 600000))

            val budget = budgetRepository.findYearlyBudget(memberId, year)
            assertThat(budget!!.amount).isEqualTo(600000)
            assertThat(budgetRepository.findByMemberIdAndYear(memberId, year)).hasSize(1)
        }
    }

    @Nested
    inner class SetMonthly {

        @Test
        fun `월별 예산을 처음 설정하면 저장된다`() {
            service.setMonthly(memberId, SetMonthlyBudgetRequest(year, 4, 300000))

            val budget = budgetRepository.findByMemberIdAndYearAndMonth(memberId, year, 4)
            assertThat(budget).isNotNull
            assertThat(budget!!.amount).isEqualTo(300000)
        }

        @Test
        fun `월별 예산을 재설정하면 덮어쓴다`() {
            service.setMonthly(memberId, SetMonthlyBudgetRequest(year, 4, 300000))
            service.setMonthly(memberId, SetMonthlyBudgetRequest(year, 4, 400000))

            val budget = budgetRepository.findByMemberIdAndYearAndMonth(memberId, year, 4)
            assertThat(budget!!.amount).isEqualTo(400000)
        }
    }
}
