package com.example.demo.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

@Configuration
class JacksonConfig {

    @Bean
    fun kotlinModule() = KotlinModule.Builder()
        .configure(KotlinFeature.NullIsSameAsDefault, true)
        .build()
}
