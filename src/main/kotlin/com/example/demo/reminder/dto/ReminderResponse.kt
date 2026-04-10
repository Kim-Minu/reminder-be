package com.example.demo.reminder.dto

import com.example.demo.reminder.domain.Priority
import com.example.demo.reminder.domain.Reminder
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ReminderResponse(
    val id: Long,
    val listId: Long,
    val title: String,
    val notes: String?,
    @get:JsonProperty("isCompleted") val isCompleted: Boolean,
    @get:JsonProperty("isFlagged") val isFlagged: Boolean,
    val priority: Priority,
    val dueDate: LocalDate?,
    val dueTime: LocalTime?,
    val completedAt: LocalDateTime?,
    val displayOrder: Int,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(domain: Reminder) = ReminderResponse(
            id = domain.id,
            listId = domain.reminderList.id,
            title = domain.title,
            notes = domain.notes,
            isCompleted = domain.isCompleted,
            isFlagged = domain.isFlagged,
            priority = domain.priority,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            completedAt = domain.completedAt,
            displayOrder = domain.displayOrder,
            createdAt = domain.createdAt,
            modifiedAt = domain.modifiedAt,
        )
    }
}
