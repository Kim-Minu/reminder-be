package com.example.demo.cart.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CartItemRequest(
    @field:NotBlank val name: String,
    @field:Min(1) val quantity: Int = 1,
    @field:Min(0) val unitPrice: Int = 0,
)
