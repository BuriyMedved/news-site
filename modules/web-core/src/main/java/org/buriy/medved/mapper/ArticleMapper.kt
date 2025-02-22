package org.buriy.medved.mapper

import org.buriy.medved.dto.ArticleDto
import org.buriy.medved.entities.Article
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ArticleMapper {
    fun toDto(entity: Article): ArticleDto
    fun toEntity(dto: ArticleDto): Article
}