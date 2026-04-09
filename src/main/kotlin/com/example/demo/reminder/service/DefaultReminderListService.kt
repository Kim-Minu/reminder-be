package com.example.demo.reminder.service

import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.repository.ReminderListRepository
import com.example.demo.reminder.service.ports.inp.ReminderListService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefaultReminderListService(
    private val reminderListRepository: ReminderListRepository
) : ReminderListService {

    override fun findAll(memberId: Long): List<ReminderList> =
        reminderListRepository.findAllByMemberIdOrderByDisplayOrderAsc(memberId)

    override fun findById(id: Long, memberId: Long): ReminderList =
        reminderListRepository.findByIdAndMemberId(id, memberId)
            ?: throw NoSuchElementException("ReminderList not found: $id")

    @Transactional
    override fun create(memberId: Long, name: String, color: String): ReminderList {
        val displayOrder = reminderListRepository.countByMemberId(memberId).toInt()
        return reminderListRepository.save(
            ReminderList(memberId = memberId, name = name, color = color, displayOrder = displayOrder)
        )
    }

    @Transactional
    override fun update(id: Long, memberId: Long, name: String, color: String, displayOrder: Int): ReminderList {
        val reminderList = findById(id, memberId)
        reminderList.update(name = name, color = color, displayOrder = displayOrder)
        return reminderList
    }

    @Transactional
    override fun delete(id: Long, memberId: Long) {
        val reminderList = findById(id, memberId)
        reminderListRepository.delete(reminderList)
    }
}
