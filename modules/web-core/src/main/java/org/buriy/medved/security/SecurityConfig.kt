package org.buriy.medved.security

import com.vaadin.flow.spring.security.VaadinWebSecurity
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter
import org.springframework.security.web.session.DisableEncodeUrlFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@EnableWebSecurity
@Profile("prod")
class SecurityConfig : VaadinWebSecurity() {
    @Value("\${spring.websecurity.debug:false}")
    var webSecurityDebug: Boolean = false

    companion object {
        private val logger = LoggerFactory.getLogger(SecurityConfig::class.java)
        @Bean
        fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
            return GrantedAuthorityDefaults("SCOPE_")
        }
    }

    @Throws(java.lang.Exception::class)
    override fun configure(http: HttpSecurity) {
        // Delegating the responsibility of general configurations
        // of http security to the super class. It's configuring
        // the followings: Vaadin's CSRF protection by ignoring
        // framework's internal requests, default request cache,
        // ignoring public views annotated with @AnonymousAllowed,
        // restricting access to other views/endpoints, and enabling
        // ViewAccessChecker authorization.
        // You can add any possible extra configurations of your own
        // here (the following is just an example):

        // http.rememberMe().alwaysRemember(false);

        // Configure your static resources with public access before calling
        // super.configure(HttpSecurity) as it adds final anyRequest matcher

        http
            .addFilterBefore({ servletRequest, servletResponse, filterChain ->
                val httpServletRequest = servletRequest as HttpServletRequest
                val session = httpServletRequest.getSession(false)
                val sesssionID = if(session != null) {
                    session.id
                }
                else{
                    "nosession"
                }

                if(logger.isDebugEnabled) {
                    logger.debug(
                        "Старт цепочки фильтров. " +
                                "${httpServletRequest.requestURL}${httpServletRequest.queryString ?: ""} " +
                                "${Thread.currentThread().name} " +
                                sesssionID
                    )
                }
                filterChain.doFilter(servletRequest, servletResponse)
            }, DisableEncodeUrlFilter::class.java)
            .addFilterAfter({ servletRequest, servletResponse, filterChain ->
                if(SecurityContextHolder.getContext().authentication != null) {
                    if(logger.isDebugEnabled) {
                        logger.debug("После сохранения аутентификации в threadLocal ${SecurityContextHolder.getContext().authentication} ${Thread.currentThread().name}")
                    }
                }
                filterChain.doFilter(servletRequest, servletResponse)
            }, SecurityContextHolderAwareRequestFilter::class.java)
            .addFilterAfter({ servletRequest, servletResponse, filterChain ->
                if(logger.isDebugEnabled) {
                    logger.debug("Конец цепочки фильтров. ${SecurityContextHolder.getContext().authentication} ${Thread.currentThread().name}")
                }
                filterChain.doFilter(servletRequest, servletResponse)
            }, AuthorizationFilter::class.java)
        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(
                AntPathRequestMatcher("/line-awesome/**"),
                AntPathRequestMatcher("/img/**")
            )
            .permitAll()
        }
        .oauth2ResourceServer {
            oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> -> oauth2.jwt(Customizer.withDefaults())
        }

        super.configure(http)

        // This is important to register your login view to the
        // view access checker mechanism:
//        setLoginView(http, LoginView::class.java)
    }

//    override fun enableNavigationAccessControl(): Boolean {
//        return false
//    }

//    @Bean
//    override fun webSecurityCustomizer(): WebSecurityCustomizer {
////        super.webSecurityCustomizer()
//        return WebSecurityCustomizer { web: WebSecurity -> web.debug(webSecurityDebug) }
//    }

    override fun configure(web: WebSecurity) {
        web.debug(webSecurityDebug)
    }

    @Bean
    fun commentsWebClient(): WebClient {
        val builder = WebClient.builder()
        return builder.build()
    }
}