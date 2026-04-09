package com.example.demo.common.security

import com.example.demo.common.security.principal.CustomPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {

    override fun createSecurityContext(annotation: WithMockCustomUser): SecurityContext {
        val principal = CustomPrincipal(memberId = annotation.memberId, email = annotation.email)
        val auth = UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        return SecurityContextHolder.createEmptyContext().also { it.authentication = auth }
    }
}
