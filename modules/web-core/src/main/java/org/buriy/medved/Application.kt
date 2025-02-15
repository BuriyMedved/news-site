package org.buriy.medved

import com.vaadin.flow.component.page.AppShellConfigurator
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application: AppShellConfigurator

//    private val log = LoggerFactory.getLogger(Application::class.java)
    fun main(args: Array<String>) {
        runApplication<Application>(*args)
    }