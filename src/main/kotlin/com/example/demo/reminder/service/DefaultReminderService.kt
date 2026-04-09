package com.example.demo.reminder.service

import com.example.demo.member.dto.RegisterRequest
import com.example.demo.reminder.domain.Reminder
import com.example.demo.reminder.dto.ReminderRequest
import com.example.demo.reminder.dto.ReminderResponse
import com.example.demo.reminder.repository.ReminderListRepository
import com.example.demo.reminder.repository.ReminderRepository
import com.example.demo.reminder.service.ports.inp.ReminderListService
import com.example.demo.reminder.service.ports.inp.ReminderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.map

@Service
@Transactional(readOnly = true)
class DefaultReminderService(
    private val reminderRepository: ReminderRepository,
    private val reminderListRepository: ReminderListRepository
) : ReminderService {

    override fun findByListId(listId: Long, memberId: Long): List<ReminderResponse> {

        return reminderRepository.findByReminderListIdOrderByDisplayOrderAsc(listId)
            .map { ReminderResponse.from(it) }.toList()
    }

    @Transactional
    override fun create(listId: Long, memberId: Long, request: ReminderRequest): ReminderResponse {

        val reminderList = reminderListRepository.findByIdAndMemberId(listId, memberId)
            ?: throw NoSuchElementException("ReminderList not found: $listId")

        // 목록 소유권 검증
        val displayOrder = reminderRepository.countByReminderListId(listId).toInt()

        val reminder = reminderRepository.save(
            request.toEntity(reminderList = reminderList, displayOrder = displayOrder)
        )

        return ReminderResponse.from(reminder)
    }
}
