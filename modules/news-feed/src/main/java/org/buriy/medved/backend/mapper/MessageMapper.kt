package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.MessageDto
import org.buriy.medved.backend.entities.Message
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface MessageMapper {
    fun toDto(entity: Message): MessageDto
    fun toEntity(dto: MessageDto): Message
}