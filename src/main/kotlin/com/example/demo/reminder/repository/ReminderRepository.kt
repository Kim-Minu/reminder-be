package com.example.demo.reminder.repository

import com.example.demo.reminder.domain.Reminder
import org.springframework.data.jpa.repository.JpaRepository

interface ReminderRepository : JpaRepository<Reminder, Long> {
    fun findByReminderListIdOrderByDisplayOrderAsc(listId: Long): List<Reminder>
    fun countByReminderListId(listId: Long): Long
}
