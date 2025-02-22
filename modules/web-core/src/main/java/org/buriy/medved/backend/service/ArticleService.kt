package org.buriy.medved.backend.service

import org.buriy.medved.backend.dto.ArticleDto
import org.buriy.medved.backend.mapper.ArticleMapper
import org.buriy.medved.backend.repository.ArticleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArticleService (val articleMapper: ArticleMapper,
                      val articleRepository: ArticleRepository
) {
    fun findAll(): List<ArticleDto> {
        return articleRepository.findAll().map { article ->  articleMapper.toDto(article)}
    }

    fun findArticleImageById(articleId: UUID): ByteArray? {
        return articleRepository.findArticleImageById(articleId)
    }
}