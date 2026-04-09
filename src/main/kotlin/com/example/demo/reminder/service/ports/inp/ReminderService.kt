package com.example.demo.reminder.service.ports.inp

import com.example.demo.reminder.domain.Reminder
import com.example.demo.reminder.dto.ReminderRequest
import com.example.demo.reminder.dto.ReminderResponse

interface ReminderService {
    fun findByListId(listId: Long, memberId: Long): List<ReminderResponse>
    fun create(listId: Long, memberId: Long, request: ReminderRequest): ReminderResponse
}
