package org.buriy.medved.backend.rest.v1

import org.buriy.medved.backend.kafka.Tags
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController: BaseApiController() {
    @GetMapping("/tags")
    fun getTags(): List<String> {
        return Tags.entries.map { tag: Tags -> tag.name }.toList()
    }
}