package org.buriy.medved

import org.buriy.medved.backend.routes.LoggingGlobalPreFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@EnableWebFluxSecurity
@Profile("prod")
class OAuth2ClientSecurityConfig {
    @Value("\${spring.websecurity.debug:false}")
    var webSecurityDebug: Boolean = false

    @Bean
    @Throws(Exception::class)
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { authorizeRequests ->

                authorizeRequests.pathMatchers(
                    "/login**","/callback/", "/error**", "/oauth2/**", "/favicon.ico", "/userinfo"
                ).permitAll()
                //все остальное только для аутентифицированных
                .anyExchange().authenticated()

            }
            .csrf {
                it.disable()
            }
            .oauth2Login { oauth2Login ->
                oauth2Login.loginPage("/oauth2/authorization/articles-client-oidc")
            }
            .oauth2Client(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun webClient(
        authorizedClientManager: ReactiveOAuth2AuthorizedClientManager,
    ): WebClient {
        val filter =
            ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)

        val builder = WebClient.builder()
        builder.filter(filter)
        return builder.build()
    }

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ReactiveClientRegistrationRepository?,
        authorizedClientRepository: ServerOAuth2AuthorizedClientRepository?
    ): ReactiveOAuth2AuthorizedClientManager {
        val authorizedClientProvider =
            ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .build()
        val authorizedClientManager = DefaultReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        return authorizedClientManager
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(webSecurityDebug) }
    }

    @Bean
    fun customFilter(): GlobalFilter {
        return LoggingGlobalPreFilter()
    }
}