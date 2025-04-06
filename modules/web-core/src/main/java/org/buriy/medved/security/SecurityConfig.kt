package org.buriy.medved.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@Profile("prod")
class SecurityConfig {
    @Value("\${spring.websecurity.debug:false}")
    var webSecurityDebug: Boolean = false

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http.securityMatcher("/api/v1/articles/**")
        http.securityMatcher("/articles/**")
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().hasAuthority("SCOPE_articles.read")
            }
            .oauth2ResourceServer {
                oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> -> oauth2.jwt(Customizer.withDefaults())
            }

        val build = http.build()
        return build
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(webSecurityDebug) }
    }
}