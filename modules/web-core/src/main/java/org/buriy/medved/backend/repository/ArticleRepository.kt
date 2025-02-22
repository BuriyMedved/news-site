package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entities.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ArticleRepository : JpaRepository<Article, UUID>, JpaSpecificationExecutor<Article> {
    @Query("select p.image from Article as p where p.id = :articleId")
    fun findArticleImageById(@Param(value = "articleId") articleId: UUID): ByteArray?
}