package org.buriy.medved

import org.buriy.medved.backend.routes.LoggingGlobalPreFilter
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@Profile("noSecurity")
class ApplicationNoSecurity {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers(AntPathRequestMatcher("/**"))
        }
    }

    @Bean
    @Throws(Exception::class)
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { authorizeRequests ->
                authorizeRequests.anyExchange().permitAll()
            }
            .csrf {
                it.disable()
            }
        return http.build()
    }

    @Bean
    fun webClient(): WebClient {
        val builder = WebClient.builder()
        return builder.build()
    }

//    @Bean
//    fun customFilter(): GlobalFilter {
//        return LoggingGlobalPreFilter()
//    }

    @Bean
    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator {
        val routeBuilder = builder.routes()
            .route("articles-service") { predicateSpec: PredicateSpec ->
                predicateSpec
                    .path("/**")
                    .uri("http://localhost:9080/**")
            }
        return routeBuilder.build()
    }
}