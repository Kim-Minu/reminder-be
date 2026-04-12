package com.example.demo.budget.dto

data class BudgetMonthResponse(
    val month: Int,
    val amount: Int?,    // null = yearlyAmount 적용
    val cartTotal: Int,
)

data class BudgetYearResponse(
    val year: Int,
    val yearlyAmount: Int?,
    val months: List<BudgetMonthResponse>,
)
