package com.example.demo.member.dto

import com.example.demo.member.domain.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.crypto.password.PasswordEncoder

data class RegisterRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    val password: String,

    @field:NotBlank
    val name: String,
) {
    fun toEntity(passwordEncoder: PasswordEncoder): Member =
        Member(
            email = email,
            name = name,
            password = passwordEncoder.encode(password)!!,
        )
}