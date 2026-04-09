package com.example.demo.reminder.dto

import com.example.demo.reminder.domain.Reminder
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ReminderResponse(
    val id: Long,
    val listId: Long,
    val title: String,
    @get:JsonProperty("isCompleted") val isCompleted: Boolean,
    val displayOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(domain: Reminder) = ReminderResponse(
            id = domain.id,
            listId = domain.reminderList.id,
            title = domain.title,
            isCompleted = domain.isCompleted,
            displayOrder = domain.displayOrder,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
