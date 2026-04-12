package com.example.demo.budget.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BudgetTest {

    private fun makeYearlyBudget() = Budget(memberId = 1L, year = 2026, month = null, amount = 500000)
    private fun makeMonthlyBudget() = Budget(memberId = 1L, year = 2026, month = 4, amount = 300000)

    @Nested
    inner class Constructor {

        @Test
        fun `연간 Budget이 정상적으로 생성된다`() {
            val budget = makeYearlyBudget()

            assertThat(budget.year).isEqualTo(2026)
            assertThat(budget.month).isNull()
            assertThat(budget.amount).isEqualTo(500000)
        }

        @Test
        fun `월별 Budget이 정상적으로 생성된다`() {
            val budget = makeMonthlyBudget()

            assertThat(budget.year).isEqualTo(2026)
            assertThat(budget.month).isEqualTo(4)
            assertThat(budget.amount).isEqualTo(300000)
        }
    }

    @Nested
    inner class UpdateAmount {

        @Test
        fun `updateAmount 호출 시 amount가 변경된다`() {
            val budget = makeYearlyBudget()

            budget.updateAmount(600000)

            assertThat(budget.amount).isEqualTo(600000)
        }

        @Test
        fun `updateAmount 호출 시 modifiedAt이 갱신된다`() {
            val budget = makeYearlyBudget()
            val before = budget.modifiedAt

            Thread.sleep(5)
            budget.updateAmount(600000)

            assertThat(budget.modifiedAt).isAfterOrEqualTo(before)
        }
    }
}
