package org.buriy.medved.backend.security

import com.vaadin.flow.spring.security.AuthenticationContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

class SecurityTools {
    companion object {
        private const val ANONYMOUS_LABEL = "Аноним"

        fun getUserName(authenticationContext : AuthenticationContext): String {
            val authenticated = authenticationContext.isAuthenticated
            val userName = if (authenticated && authenticationContext.principalName.isPresent) {
                authenticationContext.principalName.get()
            } else {
                ANONYMOUS_LABEL
            }
            return userName
        }

        fun getUserID(): String {
            val token = SecurityContextHolder.getContext().authentication.credentials as Jwt
            return token.claims["user-id"] as String
        }
    }
}