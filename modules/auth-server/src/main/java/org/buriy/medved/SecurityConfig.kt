package org.buriy.medved

import org.buriy.medved.backend.consts.RolesNames
import org.buriy.medved.backend.entities.Role
import org.buriy.medved.backend.repository.RoleRepository
import org.buriy.medved.backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import kotlin.collections.ArrayList


@Configuration
@EnableWebSecurity
@EnableTransactionManagement
class SecurityConfig(
    private val roleRepository: RoleRepository,
    private val userService: UserService,
) {
    companion object{
        private val logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    }
    @Value("\${spring.websecurity.debug:false}")
    var webSecurityDebug: Boolean = false

    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer()

        http
            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
            .with(
                authorizationServerConfigurer
            ) { authorizationServer: OAuth2AuthorizationServerConfigurer ->
                authorizationServer.oidc(Customizer.withDefaults())
            } // Enable OpenID Connect 1.0

            .authorizeHttpRequests{ authorize ->
                authorize.anyRequest().authenticated()
            }
            //Redirect to the OAuth 2.0 Login endpoint when not authenticated
            //from the authorization endpoint
            .exceptionHandling { exceptions: ExceptionHandlingConfigurer<HttpSecurity?> ->
                exceptions
                    .defaultAuthenticationEntryPointFor(
                        LoginUrlAuthenticationEntryPoint("/login"),
                        MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
            }

        return http.formLogin(Customizer.withDefaults()).build()
    }

    @Bean
    fun jwtTokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext ->
            if (OAuth2TokenType.ACCESS_TOKEN == context.tokenType) {
                val userRoles: Set<String> = AuthorityUtils.authorityListToSet(context.getPrincipal<UsernamePasswordAuthenticationToken>().authorities)

                val userRolesNoPrefix: MutableSet<String> = HashSet()
                userRoles.forEach {
                    val noPrefix = it.replace("ROLE_", "")
                    userRolesNoPrefix.add(noPrefix)
                }
                val finalSet: MutableSet<String> = HashSet()

                context.authorizedScopes.forEach {
                    if(userRolesNoPrefix.contains(it) || it.equals("openid")) {
                        finalSet.add(it)
                    }
                }
                var userRolesCommaSeparated = ""
                finalSet.forEach {
                    userRolesCommaSeparated = "$it $userRolesCommaSeparated"
                }
                if (userRolesCommaSeparated.isNotEmpty()) {
                    userRolesCommaSeparated = userRolesCommaSeparated.substring(0, userRolesCommaSeparated.length - 1)
                }

                val principal = context.getPrincipal<UsernamePasswordAuthenticationToken>()
                if(logger.isDebugEnabled){
                    logger.debug("Пользователь: $principal Роли: $userRolesCommaSeparated")
                }

                context.claims.claims { claims: MutableMap<String?, Any?> ->
                    claims["user-roles"] = userRolesCommaSeparated
                    val userDtoOptional = userService.findUserByLogin(principal.name)
                    if (userDtoOptional.isPresent) {
                        claims["user-id"] = userDtoOptional.get().id
                    }
                }
            }
        }
    }

    @Bean
    @Order(2)
    @Throws(java.lang.Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests{
                authorizeRequests -> authorizeRequests.requestMatchers(
                    "/home", "/login**","/callback/", "/webjars/**", "/error**", "/oauth2/**", "/favicon.ico", "/api/v1/**"
                ).anonymous()
                .anyRequest().authenticated()
        }
        .csrf {
            it.disable()
        }
        .formLogin(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun users(): UserDetailsService {
        for (entry in RolesNames.entries) {
            val optionalRole = roleRepository.findByName(entry.value)
            if(optionalRole.isPresent){
                continue
            }

            val role = Role(entry.value)
            roleRepository.save(role)
        }

        val mutableList = ArrayList<UserDetails>()
        userService.findAll(true).forEach { user ->
            val userDetails: UserDetails = User.builder()
                .username(user.login)
                //пароли уже зашифрованы в базе, тут не нужно дополнительно кодировать
                .password(user.password)
                .roles(*user.roles.toTypedArray())
                .build()
            mutableList.add(userDetails)
        }

        return InMemoryUserDetailsManager(mutableList)
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(webSecurityDebug) }
    }

    @Bean
	fun authorizationServerSettings(): AuthorizationServerSettings {
		return AuthorizationServerSettings.builder().build()
	}
}