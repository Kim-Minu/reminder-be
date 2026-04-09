package com.example.demo.member.service.ports.inp

import com.example.demo.member.domain.Member
import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.RefreshRequest
import com.example.demo.member.dto.RegisterRequest
import com.example.demo.member.dto.TokenResponse

interface MemberService {
    fun register(request: RegisterRequest): Member
    fun login(request: LoginRequest): TokenResponse
    fun refresh(request: RefreshRequest): TokenResponse
    fun logout(request: RefreshRequest)
}
