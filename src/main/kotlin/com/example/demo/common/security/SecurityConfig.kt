package com.example.demo.common.security

import com.example.demo.common.security.jwt.JwtAuthenticationFilter
import com.example.demo.common.security.jwt.JwtTokenProvider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val jwtFilter = JwtAuthenticationFilter(jwtTokenProvider)

        http
            .cors{cors -> cors.configurationSource(corsConfigurationSource())} // ← 핵심
            .csrf{csrf -> csrf.disable()}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/h2-console/**").permitAll()
                it.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                it.anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                }
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .headers { it.frameOptions { fo -> fo.sameOrigin() } }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.setAllowedOrigins(mutableListOf<String?>("http://localhost:3000", "http://0.0.0.0:3000", "http://192.168.0.21:3000") as List<String>?)
        config.setAllowedMethods(mutableListOf<String?>("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") as List<String>?)
        config.setAllowedHeaders(mutableListOf<String?>("*") as List<String>?)
        config.setAllowCredentials(true) // 쿠키/인증 사용 시

        val source: UrlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
