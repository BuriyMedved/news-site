package org.buriy.medved.comments.backend.rest.v1

import org.buriy.medved.comments.backend.dto.CommentDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
class CommentsController: BaseApiController() {
    @GetMapping("/comments")
    fun getComments(@RequestParam articleId: String): List<CommentDto> {
        return listOf(CommentDto(UUID.randomUUID(), "text", LocalDateTime.now(), UUID.randomUUID()))
    }
}
