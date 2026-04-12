package com.example.demo.budget.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

data class SetYearlyBudgetRequest(
    @field:Positive val year: Int,
    @field:Min(0) val amount: Int,
)
