package org.buriy.medved.comments.backend.rest.v1

import org.buriy.medved.comments.backend.dto.CommentDto
import org.buriy.medved.comments.backend.dto.CommentSearchDto
import org.buriy.medved.comments.backend.service.CommentService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class CommentsController(val commentService: CommentService): BaseApiController() {
    @GetMapping("/commentsByArticle")
    fun getCommentsByArticle(@RequestParam articleId: String): List<CommentDto> {
        return commentService.findAllForArticle(CommentSearchDto(null, UUID.fromString(articleId)))
    }

    @GetMapping("/commentsByArticleCount")
    fun getCommentsByArticleCount(@RequestParam articleId: String): Long {
        return commentService.findAllForArticleCount(CommentSearchDto(null, UUID.fromString(articleId)))
    }

    @PostMapping("/save")
    fun saveComment(@RequestBody dto: CommentDto) {
        commentService.save(dto)
    }
}
