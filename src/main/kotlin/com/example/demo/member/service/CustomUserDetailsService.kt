package com.example.demo.member.service

import com.example.demo.member.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,
) : UserDetailsService {

    // username = memberId (String)
    override fun loadUserByUsername(username: String): UserDetails {
        val member = memberRepository.findById(username.toLong())
            .orElseThrow { UsernameNotFoundException("Member not found: $username") }
        return User(
            member.id.toString(),
            member.password,
            listOf(SimpleGrantedAuthority("ROLE_${member.role}")),
        )
    }
}
