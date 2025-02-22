package org.buriy.medved.backend.rest

import org.buriy.medved.backend.service.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping("/image")
    fun getImage(@RequestParam id: String): ResponseEntity<ByteArray> {
        val imageById = articleService.findArticleImageById(UUID.fromString(id))
        return ResponseEntity.ok(imageById)
    }
}