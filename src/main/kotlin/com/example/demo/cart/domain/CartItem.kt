package com.example.demo.cart.domain

import com.example.demo.common.domain.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cart_items")
class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_week_id", nullable = false)
    val cartWeek: CartWeek,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(nullable = false)
    var unitPrice: Int = 0,

    @Column(nullable = false)
    var isChecked: Boolean = false,

    @Column(nullable = false)
    var displayOrder: Int = 0,
) : BaseTimeEntity() {

    fun update(name: String, quantity: Int, unitPrice: Int) {
        this.name = name
        this.quantity = quantity
        this.unitPrice = unitPrice
        this.modifiedAt = LocalDateTime.now()
    }

    fun toggleCheck() {
        this.isChecked = !this.isChecked
        this.modifiedAt = LocalDateTime.now()
    }
}
