package com.example.demo.member.controller

import com.example.demo.common.security.resolver.CurrentMember
import com.example.demo.member.dto.MemberResponse
import com.example.demo.member.service.ports.inp.MemberService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping("/me")
    fun me(@CurrentMember memberId: Long): MemberResponse =
        memberService.me(memberId)
}