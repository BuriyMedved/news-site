package org.buriy.medved.comments.backend.mapper

import org.buriy.medved.comments.backend.dto.CommentDto
import org.buriy.medved.comments.backend.entities.Comment
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface CommentMapper {
    fun toDto(entity: Comment): CommentDto
}