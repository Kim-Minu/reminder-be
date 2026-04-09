package com.example.demo.reminder.service.ports.inp

import com.example.demo.reminder.domain.ReminderList

interface ReminderListService {

    fun findAll(memberId: Long): List<ReminderList>

    fun findById(id: Long, memberId: Long): ReminderList

    fun create(memberId: Long, name: String, color: String = "#007AFF"): ReminderList

    fun update(id: Long, memberId: Long, name: String, color: String, displayOrder: Int): ReminderList

    fun delete(id: Long, memberId: Long)
}
