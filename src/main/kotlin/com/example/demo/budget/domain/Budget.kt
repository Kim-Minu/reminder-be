package com.example.demo.budget.domain

import com.example.demo.common.domain.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "budgets",
    uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "budget_year", "budget_month"])]
)
class Budget(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val memberId: Long,

    @Column(name = "budget_year", nullable = false)
    val year: Int,

    @Column(name = "budget_month", nullable = true)
    val month: Int? = null, // null = 연간 기본값

    @Column(nullable = false)
    var amount: Int,
) : BaseTimeEntity() {

    fun updateAmount(amount: Int) {
        this.amount = amount
        this.modifiedAt = LocalDateTime.now()
    }
}
