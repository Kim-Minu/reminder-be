package com.example.demo.common

import com.example.demo.common.security.resolver.CurrentMemberArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val currentMemberArgumentResolver: CurrentMemberArgumentResolver) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://0.0.0.0:3000", "http://192.168.0.21:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
    }

    override fun addArgumentResolvers(
        resolvers: MutableList<HandlerMethodArgumentResolver>
    ) {
        resolvers.add(currentMemberArgumentResolver)
    }
}
