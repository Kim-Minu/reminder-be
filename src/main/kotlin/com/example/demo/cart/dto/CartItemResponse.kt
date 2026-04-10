package com.example.demo.cart.dto

import com.example.demo.cart.domain.CartItem
import com.fasterxml.jackson.annotation.JsonProperty

data class CartItemResponse(
    val id: Long,
    val name: String,
    val quantity: Int,
    val unitPrice: Int,
    val lineTotal: Int,
    @get:JsonProperty("isChecked") val isChecked: Boolean,
    val displayOrder: Int,
) {
    companion object {
        fun from(item: CartItem) = CartItemResponse(
            id = item.id,
            name = item.name,
            quantity = item.quantity,
            unitPrice = item.unitPrice,
            lineTotal = item.quantity * item.unitPrice,
            isChecked = item.isChecked,
            displayOrder = item.displayOrder,
        )
    }
}
