package com.example.demo.budget.controller

import com.example.demo.budget.dto.BudgetYearResponse
import com.example.demo.budget.dto.SetMonthlyBudgetRequest
import com.example.demo.budget.dto.SetYearlyBudgetRequest
import com.example.demo.budget.service.ports.inp.BudgetService
import com.example.demo.common.security.resolver.CurrentMember
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/budgets")
class BudgetController(
    private val budgetService: BudgetService,
) {

    @GetMapping
    fun getByYear(
        @RequestParam year: Int,
        @CurrentMember memberId: Long,
    ): BudgetYearResponse = budgetService.getByYear(memberId, year)

    @PutMapping("/yearly")
    fun setYearly(
        @Valid @RequestBody request: SetYearlyBudgetRequest,
        @CurrentMember memberId: Long,
    ): BudgetYearResponse = budgetService.setYearly(memberId, request)

    @PutMapping("/monthly")
    fun setMonthly(
        @Valid @RequestBody request: SetMonthlyBudgetRequest,
        @CurrentMember memberId: Long,
    ): BudgetYearResponse = budgetService.setMonthly(memberId, request)
}
