package com.example.demo.cart.repository

import com.example.demo.cart.domain.CartWeek

interface CartWeekRepositoryCustom {
    fun findWeeksWithItems(memberId: Long, year: Int, month: Int): List<CartWeek>
    fun findCheckedTotalsByYear(memberId: Long, year: Int): Map<Int, Int>
}
