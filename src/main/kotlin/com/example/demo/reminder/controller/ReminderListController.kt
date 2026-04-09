package com.example.demo.reminder.controller

import com.example.demo.common.security.resolver.CurrentMember
import com.example.demo.reminder.dto.CreateReminderListRequest
import com.example.demo.reminder.dto.ReminderListResponse
import com.example.demo.reminder.dto.UpdateReminderListRequest
import com.example.demo.reminder.service.ports.inp.ReminderListService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reminder-lists")
class ReminderListController(private val reminderListService: ReminderListService) {

    @GetMapping
    fun getAll(@CurrentMember memberId: Long): List<ReminderListResponse> =
        reminderListService.findAll(memberId)

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @CurrentMember memberId: Long
    ): ReminderListResponse = reminderListService.findById(id, memberId)


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateReminderListRequest,
        @CurrentMember memberId: Long
    ): ReminderListResponse = reminderListService.create(memberId, request)


    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateReminderListRequest,
        @CurrentMember memberId: Long
    ): ReminderListResponse = reminderListService.update(id, memberId, request)


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
        @CurrentMember memberId: Long
    ) = reminderListService.delete(id, memberId)
}

