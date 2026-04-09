package com.example.demo.member.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val token: String,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val expiresAt: LocalDateTime,
) {
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime

    init {
        createdAt = LocalDateTime.now()
    }

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
}
