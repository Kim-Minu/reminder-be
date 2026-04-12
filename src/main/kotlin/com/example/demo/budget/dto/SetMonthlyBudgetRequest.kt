package com.example.demo.budget.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

data class SetMonthlyBudgetRequest(
    @field:Positive val year: Int,
    @field:Min(1) @field:Max(12) val month: Int,
    @field:Min(0) val amount: Int,
)
