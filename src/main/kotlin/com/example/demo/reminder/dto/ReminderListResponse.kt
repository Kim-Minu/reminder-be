package com.example.demo.reminder.dto

import com.example.demo.reminder.domain.ReminderList
import java.time.LocalDateTime

data class ReminderListResponse(
    val id: Long,
    val name: String,
    val color: String,
    val displayOrder: Int,
    val reminderCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(domain: ReminderList) = ReminderListResponse(
            id = domain.id,
            name = domain.name,
            color = domain.color,
            displayOrder = domain.displayOrder,
            reminderCount = domain.reminders.size,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
