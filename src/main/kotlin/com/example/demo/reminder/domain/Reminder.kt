package com.example.demo.reminder.domain

import com.example.demo.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
) : BaseEntity() {

    @Column
    var notes: String? = null

    @Column(nullable = false)
    var isFlagged: Boolean = false

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: Priority = Priority.NONE

    @Column
    var dueDate: LocalDate? = null

    @Column
    var dueTime: LocalTime? = null

    @Column
    var completedAt: LocalDateTime? = null

    fun update(
        title: String,
        notes: String?,
        isFlagged: Boolean,
        priority: Priority?,
        dueDate: LocalDate?,
        dueTime: LocalTime?,
        displayOrder: Int,
    ) {
        this.title = title
        this.notes = notes
        this.isFlagged = isFlagged
        this.priority = priority ?: Priority.NONE
        this.dueDate = dueDate
        this.dueTime = dueTime
        this.displayOrder = displayOrder
        this.modifiedAt = LocalDateTime.now()
    }

    fun toggleComplete() {
        this.isCompleted = !this.isCompleted
        this.completedAt = if (this.isCompleted) LocalDateTime.now() else null
        this.modifiedAt = LocalDateTime.now()
    }
}
