package com.example.demo.cart.repository

import com.example.demo.cart.domain.CartWeek
import org.springframework.data.jpa.repository.JpaRepository

interface CartWeekRepository : JpaRepository<CartWeek, Long>, CartWeekRepositoryCustom {
    fun findByMemberIdAndYearAndMonthAndWeekOfMonth(memberId: Long, year: Int, month: Int, weekOfMonth: Int): CartWeek?
    fun findByIdAndMemberId(id: Long, memberId: Long): CartWeek?
}
