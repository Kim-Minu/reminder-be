package com.example.demo.reminder.service.ports.inp

import com.example.demo.reminder.dto.ReminderRequest
import com.example.demo.reminder.dto.ReminderResponse

interface ReminderService {
    fun findByListId(listId: Long, memberId: Long): List<ReminderResponse>
    fun create(listId: Long, memberId: Long, request: ReminderRequest): ReminderResponse
    fun update(id: Long, memberId: Long, request: ReminderRequest): ReminderResponse
    fun delete(id: Long, memberId: Long)
    fun toggleComplete(id: Long, memberId: Long): ReminderResponse
}
