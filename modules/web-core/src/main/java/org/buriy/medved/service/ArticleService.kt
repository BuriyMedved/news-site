package org.buriy.medved.service

import org.buriy.medved.dto.ArticleDto
import org.buriy.medved.entities.Article
import org.buriy.medved.mapper.ArticleMapper
import org.buriy.medved.repository.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleService (val articleMapper: ArticleMapper,
                      val articleRepository: ArticleRepository) {
    fun findAll(): List<ArticleDto> {
        return articleRepository.findAll().map { article ->  articleMapper.toDto(article)}
    }
}