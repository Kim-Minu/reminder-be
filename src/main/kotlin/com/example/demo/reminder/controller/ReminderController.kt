package com.example.demo.reminder.controller

import com.example.demo.common.security.resolver.CurrentMember
import com.example.demo.reminder.dto.ReminderRequest
import com.example.demo.reminder.dto.ReminderResponse
import com.example.demo.reminder.service.ports.inp.ReminderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reminder-lists/{listId}/reminders")
class ReminderController(private val reminderService: ReminderService) {

    @GetMapping
    fun getByListId(
        @PathVariable listId: Long,
        @CurrentMember memberId: Long,
    ): List<ReminderResponse> =
        reminderService.findByListId(listId, memberId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable listId: Long,
        @Valid @RequestBody request: ReminderRequest,
        @CurrentMember memberId: Long,
    ): ReminderResponse = reminderService.create(listId, memberId, request)

}