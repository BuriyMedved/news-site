package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.TagDto
import org.buriy.medved.backend.entities.Tag
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface TagMapper {
    fun toDto(entity: Tag): TagDto
    fun toEntity(dto: TagDto): Tag
}