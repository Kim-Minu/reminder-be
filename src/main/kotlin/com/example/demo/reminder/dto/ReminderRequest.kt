package com.example.demo.reminder.dto

import com.example.demo.reminder.domain.Priority
import com.example.demo.reminder.domain.Reminder
import com.example.demo.reminder.domain.ReminderList
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate
import java.time.LocalTime

data class ReminderRequest(
    @field:NotBlank
    val title: String,
    val notes: String? = null,
    val isFlagged: Boolean = false,
    val priority: Priority? = null,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val displayOrder: Int = 0,
) {
    fun toEntity(reminderList: ReminderList, displayOrder: Int): Reminder =
        Reminder(
            title = title,
            reminderList = reminderList,
            displayOrder = displayOrder,
        ).also {
            it.notes = notes
            it.isFlagged = isFlagged
            it.priority = priority ?: Priority.NONE
            it.dueDate = dueDate
            it.dueTime = dueTime
        }
}
