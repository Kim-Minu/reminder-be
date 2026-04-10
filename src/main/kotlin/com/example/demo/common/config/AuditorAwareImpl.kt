package com.example.demo.common.config

import com.example.demo.common.security.principal.CustomPrincipal
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class AuditorAwareImpl : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        val principal = SecurityContextHolder.getContext()
            .authentication
            ?.takeIf { it.isAuthenticated }
            ?.principal as? CustomPrincipal
            ?: return Optional.empty()

        return Optional.of(principal.memberId.toString())
    }
}
