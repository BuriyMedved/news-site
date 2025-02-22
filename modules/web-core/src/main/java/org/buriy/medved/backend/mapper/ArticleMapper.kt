package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.ArticleDto
import org.buriy.medved.backend.entities.Article
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ArticleMapper {
    fun toDto(entity: Article): ArticleDto
    fun toEntity(dto: ArticleDto): Article
}