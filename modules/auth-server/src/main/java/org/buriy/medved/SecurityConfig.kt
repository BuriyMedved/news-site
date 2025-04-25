package org.buriy.medved

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import java.util.*


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Value("\${spring.websecurity.debug:false}")
    var webSecurityDebug: Boolean = false
//    @Bean
//    @Order(1)
//    @Throws(Exception::class)
//    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer()
//
//        http
//            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
//            .with(authorizationServerConfigurer)
//            { authorizationServer: OAuth2AuthorizationServerConfigurer ->
//                authorizationServer.oidc(Customizer.withDefaults())
//            }
//            // Enable OpenID Connect 1.0
//            .authorizeHttpRequests(
//                {
//                    authorize -> authorize.anyRequest().authenticated()
//                }
//            )
//            // Redirect to the login page when not authenticated from the
//            // authorization endpoint
//            .exceptionHandling { exceptions ->
//                exceptions
//                    .defaultAuthenticationEntryPointFor(
//                        LoginUrlAuthenticationEntryPoint("/login"),
//                        MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                    )
//            }
//
//        return http.build()
//    }
//
//    @Bean
//    @Order(2)
//    @Throws(Exception::class)
//    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .authorizeHttpRequests(
//                {
//                    authorize -> authorize.anyRequest().authenticated()
//                }
//            )
//            // Form login handles the redirect to the login page from the
//            // authorization server filter chain
//            .formLogin(Customizer.withDefaults())
//
//        return http.build()
//    }
//
//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
//        val userDetails: UserDetails = User.builder()
//            .username("user")
//            .password("password")
//            .passwordEncoder(encoder::encode)
//            .roles("USER")
//            .build()
//
//        return InMemoryUserDetailsManager(userDetails)
//    }
//
//    @Bean
//    fun registeredClientRepository(): RegisteredClientRepository {
//        val oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
//            .clientId("articles-client")
//            .clientSecret("{noop}secret")
//            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
//            .postLogoutRedirectUri("http://127.0.0.1:8080/")
//            .scope(OidcScopes.OPENID)
////            .scope(OidcScopes.PROFILE)
//            .scope("articles.read")
//            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
//            .build()
//
//        return InMemoryRegisteredClientRepository(oidcClient)
//    }
//
//    @Bean
//    fun jwkSource(): JWKSource<SecurityContext> {
//        val keyPair = generateRsaKey()
//        val publicKey = keyPair.public as RSAPublicKey
//        val privateKey = keyPair.private as RSAPrivateKey
//        val rsaKey: RSAKey = RSAKey.Builder(publicKey)
//            .privateKey(privateKey)
//            .keyID(UUID.randomUUID().toString())
//            .build()
//        val jwkSet = JWKSet(rsaKey)
//        return ImmutableJWKSet(jwkSet)
//    }
//
//    private fun generateRsaKey(): KeyPair {
//        val keyPair: KeyPair
//        try {
//            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
//            keyPairGenerator.initialize(2048)
//            keyPair = keyPairGenerator.generateKeyPair()
//        } catch (ex: Exception) {
//            throw IllegalStateException(ex)
//        }
//        return keyPair
//    }
//
//    @Bean
//    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
//        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
//    }
//
//    @Bean
//    fun authorizationServerSettings(): AuthorizationServerSettings {
//        return AuthorizationServerSettings.builder().build()
//    }

    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
//        http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults())
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults()) // Enable OpenID Connect 1.0
//        http.exceptionHandling {
//                exceptions -> exceptions.accessDeniedHandler { request, response, accessDeniedException ->
//                     println("request = [${request}], response = [${response}], accessDeniedException = [${accessDeniedException}]")
//                }
//        }
        return http.formLogin(Customizer.withDefaults()).build()
    }

    @Bean
    @Order(2)
    @Throws(java.lang.Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests{
                authorizeRequests -> authorizeRequests.requestMatchers(
                    "/home", "/login**","/callback/", "/webjars/**", "/error**", "/oauth2/**", "/favicon.ico"
                ).anonymous()
                .anyRequest().authenticated()
        }

//        http.authorizeHttpRequests {
//            authorizeRequests -> authorizeRequests.anyRequest().authenticated()
//        }
        .formLogin(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun users(): UserDetailsService {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        val user: UserDetails = User.builder()
            .username("admin")
            .password("password")
            .passwordEncoder { rawPassword: CharSequence? -> encoder.encode(rawPassword) }
//            .roles("USER")
            .roles("articles.read")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(webSecurityDebug) }
    }
}