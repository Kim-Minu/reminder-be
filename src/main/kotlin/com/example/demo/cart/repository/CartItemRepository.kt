package com.example.demo.cart.repository

import com.example.demo.cart.domain.CartItem
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByIdAndCartWeek_MemberId(id: Long, memberId: Long): CartItem?
    fun countByCartWeekId(cartWeekId: Long): Long
    fun deleteByCartWeekIdAndIsChecked(cartWeekId: Long, isChecked: Boolean)
}
