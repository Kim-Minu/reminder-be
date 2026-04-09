package com.example.demo.common.security

import com.example.demo.common.security.principal.CustomPrincipal
import org.springframework.security.core.Authentication

class SecurityExtensions {
    fun Authentication.memberId(): Long =
        (principal as CustomPrincipal).memberId
}