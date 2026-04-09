package com.example.demo.member.dto

import com.example.demo.member.domain.Member

data class MemberResponse(
    val id: Long,
    val email: String,
    val name: String,
) {
    companion object {
        fun from(domain: Member) = MemberResponse(
            id = domain.id,
            email = domain.email,
            name = domain.name,
        )
    }
}
