package com.example.demo.reminder.service.ports.inp

import com.example.demo.member.dto.RegisterRequest
import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.dto.CreateReminderListRequest
import com.example.demo.reminder.dto.ReminderListResponse
import com.example.demo.reminder.dto.UpdateReminderListRequest

interface ReminderListService {

    fun findAll(memberId: Long): List<ReminderListResponse>

    fun findById(id: Long, memberId: Long): ReminderListResponse

    fun create(memberId: Long, request: CreateReminderListRequest): ReminderListResponse

    fun update(id: Long, memberId: Long, request: UpdateReminderListRequest): ReminderListResponse

    fun delete(id: Long, memberId: Long)
}
