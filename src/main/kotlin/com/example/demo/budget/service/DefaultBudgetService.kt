package com.example.demo.budget.service

import com.example.demo.budget.domain.Budget
import com.example.demo.budget.dto.BudgetMonthResponse
import com.example.demo.budget.dto.BudgetYearResponse
import com.example.demo.budget.dto.SetMonthlyBudgetRequest
import com.example.demo.budget.dto.SetYearlyBudgetRequest
import com.example.demo.budget.repository.BudgetRepository
import com.example.demo.budget.service.ports.inp.BudgetService
import com.example.demo.cart.repository.CartWeekRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefaultBudgetService(
    private val budgetRepository: BudgetRepository,
    private val cartWeekRepository: CartWeekRepository,
) : BudgetService {

    override fun getByYear(memberId: Long, year: Int): BudgetYearResponse {
        val budgets = budgetRepository.findByMemberIdAndYear(memberId, year)
        val yearlyBudget = budgets.find { it.month == null }
        val monthlyOverrides = budgets.filter { it.month != null }.associateBy { it.month!! }
        val cartTotals = cartWeekRepository.findCheckedTotalsByYear(memberId, year)

        val months = (1..12).map { m ->
            BudgetMonthResponse(
                month = m,
                amount = monthlyOverrides[m]?.amount,
                cartTotal = cartTotals[m] ?: 0,
            )
        }

        return BudgetYearResponse(
            year = year,
            yearlyAmount = yearlyBudget?.amount,
            months = months,
        )
    }

    @Transactional
    override fun setYearly(memberId: Long, request: SetYearlyBudgetRequest): BudgetYearResponse {
        val existing = budgetRepository.findYearlyBudget(memberId, request.year)
        if (existing != null) {
            existing.updateAmount(request.amount)
        } else {
            budgetRepository.save(
                Budget(memberId = memberId, year = request.year, month = null, amount = request.amount)
            )
        }
        return getByYear(memberId, request.year)
    }

    @Transactional
    override fun setMonthly(memberId: Long, request: SetMonthlyBudgetRequest): BudgetYearResponse {
        val existing = budgetRepository.findByMemberIdAndYearAndMonth(memberId, request.year, request.month)
        if (existing != null) {
            existing.updateAmount(request.amount)
        } else {
            budgetRepository.save(
                Budget(memberId = memberId, year = request.year, month = request.month, amount = request.amount)
            )
        }
        return getByYear(memberId, request.year)
    }
}
