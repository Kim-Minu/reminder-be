package com.example.demo.common.domain

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseTimeEntity {

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime

    @Column(nullable = false)
    var modifiedAt: LocalDateTime

    init {
        val now = LocalDateTime.now()
        createdAt = now
        modifiedAt = now
    }
}
