package com.example.demo.reminder.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateReminderListRequest(
    @field:NotBlank(message = "이름은 필수입니다")
    val name: String,

    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "color는 hex 형식이어야 합니다 (예: #007AFF)")
    val color: String,

    val displayOrder: Int
)
