package com.example.demo.reminder.service

import com.example.demo.reminder.domain.ReminderList
import com.example.demo.reminder.dto.CreateReminderListRequest
import com.example.demo.reminder.dto.ReminderListResponse
import com.example.demo.reminder.dto.UpdateReminderListRequest
import com.example.demo.reminder.repository.ReminderListRepository
import com.example.demo.reminder.service.ports.inp.ReminderListService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefaultReminderListService(
    private val reminderListRepository: ReminderListRepository
) : ReminderListService {

    override fun findAll(memberId: Long): List<ReminderListResponse> =
        reminderListRepository.findAllByMemberIdOrderByDisplayOrderAsc(memberId)
            .map { ReminderListResponse.from(it) }
            .toList()

    override fun findById(id: Long, memberId: Long): ReminderListResponse {
        val reminderList = reminderListRepository.findByIdAndMemberId(id, memberId)
            ?: throw NoSuchElementException("ReminderList not found: $id")

        return ReminderListResponse.from(reminderList)
    }

    @Transactional
    override fun create(memberId: Long, request: CreateReminderListRequest): ReminderListResponse {

        val displayOrder = reminderListRepository.countByMemberId(memberId).toInt()

        return ReminderListResponse.from(
            reminderListRepository.save(
                request.toEntity(memberId, displayOrder)
            )
        )
    }

    @Transactional
    override fun update(id: Long, memberId: Long, request: UpdateReminderListRequest): ReminderListResponse {

        val reminderList = reminderListRepository.findByIdAndMemberId(id, memberId)
            ?: throw NoSuchElementException("ReminderList not found: $id")

        reminderList.update(request)

        return ReminderListResponse.from(reminderList)
    }

    @Transactional
    override fun delete(id: Long, memberId: Long) {
        val reminderList = reminderListRepository.findByIdAndMemberId(id, memberId)
            ?: throw NoSuchElementException("ReminderList not found: $id")

        reminderListRepository.delete(reminderList)
    }
}
