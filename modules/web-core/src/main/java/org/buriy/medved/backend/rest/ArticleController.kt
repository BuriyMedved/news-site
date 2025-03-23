package org.buriy.medved.backend.rest

import org.buriy.medved.backend.service.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ArticleController(
    private val articleService: ArticleService,
): BaseApiController() {
    @GetMapping("/image")
    fun getImage(@RequestParam id: String): ResponseEntity<ByteArray> {
        val imageById = articleService.findArticleImageById(UUID.fromString(id))
        return ResponseEntity.ok(imageById)
    }
    @GetMapping("/list")
    fun getList(): Array<String> {
        return arrayOf("name_one", "name_two", "name_three")
    }
}