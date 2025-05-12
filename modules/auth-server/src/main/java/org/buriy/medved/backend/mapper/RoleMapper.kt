package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.RoleDto
import org.buriy.medved.backend.entities.Role
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RoleMapper {
    fun toDto(entity: Role): RoleDto
    fun toEntity(dto: RoleDto): Role
}