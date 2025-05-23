package org.buriy.medved

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthServerApp
    fun main(args: Array<String>) {
        runApplication<AuthServerApp>(*args)
    }