package org.buriy.medved.backend.rest.v1

import org.buriy.medved.backend.dto.TagDto
import org.buriy.medved.backend.service.TagService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class TagController(
    private val tagService: TagService
): BaseApiController() {
    @GetMapping("/tags")
    fun getTags(): List<TagDto> {
        return tagService.findAll()
    }

    @GetMapping("/tagImage")
    fun getTagImage(@RequestParam tagId: String): ByteArray {
        return tagService.findTagImageById(UUID.fromString(tagId))
    }
}