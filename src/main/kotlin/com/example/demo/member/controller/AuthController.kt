package com.example.demo.member.controller

import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.MemberResponse
import com.example.demo.member.dto.RefreshRequest
import com.example.demo.member.dto.RegisterRequest
import com.example.demo.member.dto.TokenResponse
import com.example.demo.member.service.ports.inp.MemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val memberService: MemberService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): MemberResponse =
        memberService.register(request)

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): TokenResponse =
        memberService.login(request)

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): TokenResponse =
        memberService.refresh(request)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@Valid @RequestBody request: RefreshRequest) =
        memberService.logout(request)
}
