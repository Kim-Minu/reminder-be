package com.example.demo.cart.dto

import com.example.demo.cart.domain.CartWeek

data class CartWeekResponse(
    val id: Long,
    val year: Int,
    val month: Int,
    val weekOfMonth: Int,
    val label: String,
    val items: List<CartItemResponse>,
    val totalAmount: Int,
    val checkedAmount: Int,
) {
    companion object {
        fun from(week: CartWeek): CartWeekResponse {
            val items = week.items.sortedBy { it.displayOrder }.map { CartItemResponse.from(it) }
            return CartWeekResponse(
                id = week.id,
                year = week.year,
                month = week.month,
                weekOfMonth = week.weekOfMonth,
                label = week.label,
                items = items,
                totalAmount = items.sumOf { it.lineTotal },
                checkedAmount = items.filter { it.isChecked }.sumOf { it.lineTotal },
            )
        }
    }
}
