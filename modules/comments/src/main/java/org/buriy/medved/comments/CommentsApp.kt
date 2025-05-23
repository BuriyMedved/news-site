package org.buriy.medved.comments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommentsApp
    fun main(args: Array<String>) {
        runApplication<CommentsApp>(*args)
    }