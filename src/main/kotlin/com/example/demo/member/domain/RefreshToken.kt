package com.example.demo.member.domain

import com.example.demo.common.domain.BaseTimeEntity
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
) : BaseTimeEntity() {

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
}
