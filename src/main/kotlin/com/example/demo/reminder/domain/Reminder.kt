package com.example.demo.reminder.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reminders")
class Reminder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    var reminderList: ReminderList,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var isCompleted: Boolean = false,

    @Column(nullable = false)
    var displayOrder: Int = 0,
) {
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime

    @Column(nullable = false)
    var updatedAt: LocalDateTime

    init {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    fun update(title: String, isCompleted: Boolean, displayOrder: Int) {
        this.title = title
        this.isCompleted = isCompleted
        this.displayOrder = displayOrder
        this.updatedAt = LocalDateTime.now()
    }
}
