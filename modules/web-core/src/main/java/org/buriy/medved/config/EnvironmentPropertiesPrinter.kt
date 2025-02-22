package org.buriy.medved.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


@Component
class EnvironmentPropertiesPrinter {
    @Autowired
    private val env: Environment? = null

    @PostConstruct
    fun logApplicationProperties() {
//        println("((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((")
//        println(env)
//        println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))")
    }
}