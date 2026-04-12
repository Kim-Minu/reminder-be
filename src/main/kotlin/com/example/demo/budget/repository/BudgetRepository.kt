package com.example.demo.budget.repository

import com.example.demo.budget.domain.Budget
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BudgetRepository : JpaRepository<Budget, Long> {

    fun findByMemberIdAndYear(memberId: Long, year: Int): List<Budget>

    @Query("SELECT b FROM Budget b WHERE b.memberId = :memberId AND b.year = :year AND b.month IS NULL")
    fun findYearlyBudget(memberId: Long, year: Int): Budget?

    fun findByMemberIdAndYearAndMonth(memberId: Long, year: Int, month: Int): Budget?
}
