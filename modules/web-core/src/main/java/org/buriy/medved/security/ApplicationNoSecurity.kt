package org.buriy.medved.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@Profile("noSecurity")
class ApplicationNoSecurity {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(AntPathRequestMatcher("/**"))
        }
    }

    @Bean
    fun commentsWebClient(): WebClient {
        val builder = WebClient.builder()
        builder.baseUrl("http://localhost:9081")
        return builder.build()
    }
}