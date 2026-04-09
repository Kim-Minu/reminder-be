package com.example.demo.reminder.service.ports.inp

import com.example.demo.reminder.domain.Reminder

interface ReminderService {
    fun findByListId(listId: Long, memberId: Long): List<Reminder>
    fun create(listId: Long, memberId: Long, title: String): Reminder
}
