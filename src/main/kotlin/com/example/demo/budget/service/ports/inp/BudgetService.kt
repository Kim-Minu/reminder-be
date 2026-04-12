package com.example.demo.budget.service.ports.inp

import com.example.demo.budget.dto.BudgetYearResponse
import com.example.demo.budget.dto.SetMonthlyBudgetRequest
import com.example.demo.budget.dto.SetYearlyBudgetRequest

interface BudgetService {
    fun getByYear(memberId: Long, year: Int): BudgetYearResponse
    fun setYearly(memberId: Long, request: SetYearlyBudgetRequest): BudgetYearResponse
    fun setMonthly(memberId: Long, request: SetMonthlyBudgetRequest): BudgetYearResponse
}
