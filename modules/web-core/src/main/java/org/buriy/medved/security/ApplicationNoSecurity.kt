package org.buriy.medved.security

import com.vaadin.flow.spring.security.VaadinWebSecurity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@Profile("noSecurity")
class ApplicationNoSecurity : VaadinWebSecurity() {

    companion object {
        private val logger = LoggerFactory.getLogger(SecurityConfig::class.java)
        @Bean
        fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
            return GrantedAuthorityDefaults("SCOPE_")
        }
    }

//    @Throws(java.lang.Exception::class)
//    override fun configure(http: HttpSecurity) {
//
//    }
//    @Bean
//    fun webSecurityCustomizer(): WebSecurityCustomizer {
//        return WebSecurityCustomizer { web: WebSecurity ->
//            web.ignoring()
//                .requestMatchers(AntPathRequestMatcher("/**"))
//        }
//    }

    override fun configure(web: WebSecurity) {
        web.ignoring().requestMatchers(AntPathRequestMatcher("/**"))
    }

    override fun enableNavigationAccessControl(): Boolean {
        return false
    }
    @Bean
    fun commentsWebClient(): WebClient {
        val builder = WebClient.builder()
        builder.baseUrl("http://localhost:9081")
        return builder.build()
    }

    @Bean
    fun usersWebClient(): WebClient {
        val builder = WebClient.builder()
        builder.baseUrl("http://127.0.0.2:9000/api/v1/auth")
        return builder.build()
    }
}