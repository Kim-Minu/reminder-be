package com.example.demo.reminder.dto

import jakarta.validation.constraints.NotBlank

data class ReminderRequest(
    @field:NotBlank
    val title: String
)
