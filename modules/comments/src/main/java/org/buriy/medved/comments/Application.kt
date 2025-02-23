package org.buriy.medved.comments

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
//@ComponentScan(basePackages = ["org.buriy.medved.comments"])
//@ComponentScan
class Application
//    private val log = LoggerFactory.getLogger(Application::class.java)
    fun main(args: Array<String>) {
        runApplication<Application>(*args)
    }