package org.buriy.medved.mapper

import org.buriy.medved.dto.BaseDto
import org.buriy.medved.entities.BaseEntity
import org.mapstruct.Mapper

@Mapper(uses = [ArticleMapper::class])
interface BaseDtoMapper {
    fun toBaseDto(entity: BaseEntity): BaseDto
}