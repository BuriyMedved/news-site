package org.buriy.medved

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@EnableWebSecurity
class OAuth2ClientSecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests.requestMatchers(
                    "/login**","/callback/", "/webjars/**", "/error**", "/oauth2/**", "/favicon.ico"
                ).anonymous()
                .anyRequest().authenticated()
            }
            .oauth2Login { oauth2Login: OAuth2LoginConfigurer<HttpSecurity?> ->
                oauth2Login.loginPage("/oauth2/authorization/articles-client-oidc")
                oauth2Login.failureHandler{
                        request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException ->
                    println("!!!! exception " + exception.message)
                }
            }
            .oauth2Client(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun webClient(authorizedClientManager: OAuth2AuthorizedClientManager?): WebClient {
        val oauth2Client =
            ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        return WebClient.builder()
                        .apply(oauth2Client.oauth2Configuration())
                        .build()
    }

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository?,
        authorizedClientRepository: OAuth2AuthorizedClientRepository?
    ): OAuth2AuthorizedClientManager {
        val authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .build()
        val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        return authorizedClientManager
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(true) }
    }

//    @Bean
//    fun webClient(): WebClient {
//        return WebClient.builder()
//            .build()
//    }
}