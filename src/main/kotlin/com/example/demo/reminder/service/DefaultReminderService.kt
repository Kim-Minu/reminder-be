package com.example.demo.reminder.service

import com.example.demo.reminder.domain.Reminder
import com.example.demo.reminder.repository.ReminderRepository
import com.example.demo.reminder.service.ports.inp.ReminderListService
import com.example.demo.reminder.service.ports.inp.ReminderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefaultReminderService(
    private val reminderRepository: ReminderRepository,
    private val reminderListService: ReminderListService
) : ReminderService {

    override fun findByListId(listId: Long, memberId: Long): List<Reminder> {
        reminderListService.findById(listId, memberId) // 목록 소유권 검증
        return reminderRepository.findByReminderListIdOrderByDisplayOrderAsc(listId)
    }

    @Transactional
    override fun create(listId: Long, memberId: Long, title: String): Reminder {
        val reminderList = reminderListService.findById(listId, memberId) // 목록 소유권 검증
        val displayOrder = reminderRepository.countByReminderListId(listId).toInt()
        return reminderRepository.save(
            Reminder(reminderList = reminderList, title = title, displayOrder = displayOrder)
        )
    }
}
