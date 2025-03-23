package org.buriy.medved.backend.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class TestController {
    @GetMapping("/test")
    fun test(): String {
        return "test"
    }
}