package com.example.demo.cart.service.ports.inp

import com.example.demo.cart.dto.CartItemRequest
import com.example.demo.cart.dto.CartItemResponse
import com.example.demo.cart.dto.CartWeekRequest
import com.example.demo.cart.dto.CartWeekResponse

interface CartService {
    fun findWeeksByMonth(memberId: Long, year: Int, month: Int): List<CartWeekResponse>
    fun findOrCreateWeek(memberId: Long, request: CartWeekRequest): CartWeekResponse
    fun createItem(weekId: Long, memberId: Long, request: CartItemRequest): CartItemResponse
    fun updateItem(id: Long, memberId: Long, request: CartItemRequest): CartItemResponse
    fun deleteItem(id: Long, memberId: Long)
    fun toggleCheck(id: Long, memberId: Long): CartItemResponse
    fun deleteCheckedItems(weekId: Long, memberId: Long)
}
