package com.example.demo.reminder.repository

import com.example.demo.reminder.domain.ReminderList
import org.springframework.data.jpa.repository.JpaRepository

interface ReminderListRepository : JpaRepository<ReminderList, Long> {
    fun findAllByMemberIdOrderByDisplayOrderAsc(memberId: Long): List<ReminderList>
    fun findByIdAndMemberId(id: Long, memberId: Long): ReminderList?
    fun countByMemberId(memberId: Long): Long
}
