package org.buriy.medved

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NewsFeedApp
    fun main(args: Array<String>) {
        runApplication<NewsFeedApp>(*args)
    }