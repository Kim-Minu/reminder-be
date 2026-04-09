package com.example.demo.reminder.controller

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
    fun getAll(@AuthenticationPrincipal user: UserDetails): List<ReminderListResponse> =
        reminderListService.findAll(user.memberId()).map { ReminderListResponse.from(it) }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: UserDetails,
    ): ReminderListResponse =
        ReminderListResponse.from(reminderListService.findById(id, user.memberId()))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateReminderListRequest,
        @AuthenticationPrincipal user: UserDetails,
    ): ReminderListResponse =
        ReminderListResponse.from(
            reminderListService.create(user.memberId(), request.name, request.color ?: "#007AFF")
        )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateReminderListRequest,
        @AuthenticationPrincipal user: UserDetails,
    ): ReminderListResponse =
        ReminderListResponse.from(
            reminderListService.update(id, user.memberId(), request.name, request.color, request.displayOrder)
        )

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: UserDetails,
    ) = reminderListService.delete(id, user.memberId())
}


private fun UserDetails.memberId(): Long = username.toLong()
