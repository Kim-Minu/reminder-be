package com.example.demo.reminder.dto

import com.example.demo.reminder.domain.Reminder
import com.example.demo.reminder.domain.ReminderList
import jakarta.validation.constraints.NotBlank

data class ReminderRequest(
    @field:NotBlank
    val title: String
) {
    fun toEntity(reminderList: ReminderList, displayOrder: Int) : Reminder =
        Reminder (
            title = title,
            reminderList = reminderList,
            displayOrder = displayOrder
        )
}
