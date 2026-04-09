package com.example.demo.common.security.principal

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomPrincipal(
    val memberId: Long,
    val email: String,
    val authorities: Collection<GrantedAuthority> = emptyList()
) : UserDetails {

    override fun getUsername(): String = email

    override fun getPassword(): String = ""

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}