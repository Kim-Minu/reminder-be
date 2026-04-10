package com.example.demo.cart.domain

import com.example.demo.common.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "cart_weeks",
    uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "cart_year", "cart_month", "week_of_month"])]
)
class CartWeek(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val memberId: Long,

    @Column(name = "cart_year", nullable = false)
    val year: Int,

    @Column(name = "cart_month", nullable = false)
    val month: Int,

    @Column(nullable = false)
    val weekOfMonth: Int,

    @Column(nullable = false)
    val label: String,

    @OneToMany(mappedBy = "cartWeek", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CartItem> = mutableListOf(),
) : BaseTimeEntity()
