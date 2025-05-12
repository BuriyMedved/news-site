package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.BaseDto
import org.buriy.medved.backend.entities.BaseEntity
import org.mapstruct.Mapper

@Mapper(uses = [UserMapper::class])
interface BaseDtoMapper {
    fun toBaseDto(entity: BaseEntity): BaseDto
    fun toEntity(dto: BaseDto): BaseEntity
}