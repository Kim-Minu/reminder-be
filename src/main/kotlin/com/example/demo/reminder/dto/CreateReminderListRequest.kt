package com.example.demo.reminder.dto

import com.example.demo.reminder.domain.ReminderList
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateReminderListRequest(
    @field:NotBlank(message = "이름은 필수입니다")
    val name: String,

    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "color는 hex 형식이어야 합니다 (예: #007AFF)")
    val color: String? = null
) {

    fun toEntity(memberId: Long, displayOrder: Int): ReminderList =
        ReminderList(
            memberId = memberId,
            name = name,
            color = color ?: "#007AFF",
            displayOrder = displayOrder
        )
}
