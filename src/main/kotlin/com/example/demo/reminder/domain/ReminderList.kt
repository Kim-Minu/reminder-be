package com.example.demo.reminder.domain

import com.example.demo.reminder.dto.UpdateReminderListRequest
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reminder_lists")
class ReminderList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var color: String = "#007AFF",

    @Column(nullable = false)
    var displayOrder: Int = 0,

    @OneToMany(mappedBy = "reminderList", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reminders: MutableList<Reminder> = mutableListOf()
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

    fun update(request: UpdateReminderListRequest) {
        this.name = request.name
        this.color = request.color
        this.displayOrder = request.displayOrder
        this.updatedAt = LocalDateTime.now()
    }
}
