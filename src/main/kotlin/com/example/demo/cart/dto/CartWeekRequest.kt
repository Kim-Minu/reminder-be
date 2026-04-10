package com.example.demo.cart.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CartWeekRequest(
    val year: Int,
    @field:Min(1) @field:Max(12) val month: Int,
    @field:Min(1) @field:Max(5) val weekOfMonth: Int,
)
