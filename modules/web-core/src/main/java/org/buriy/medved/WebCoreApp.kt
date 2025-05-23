package org.buriy.medved

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@Push
@SpringBootApplication
class WebCoreApp: AppShellConfigurator

    fun main(args: Array<String>) {
        runApplication<WebCoreApp>(*args)
    }