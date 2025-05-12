package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.RoleDto
import org.buriy.medved.backend.dto.UserDto
import org.buriy.medved.backend.entities.Role
import org.buriy.medved.backend.entities.RoleToUserCross
import org.buriy.medved.backend.entities.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UserMapper {
    @Mapping(target = "password", constant = "")
    fun toDto(entity: User): UserDto

    fun toDtoWithPassword(entity: User): UserDto

    @Mapping(target = "roles", ignore = true)
    fun toEntity(dto: UserDto): User

    @Mapping(target = "roles", ignore = true)
    fun updateEntityFromDto(dto: UserDto, @MappingTarget entity: User)

    @Suppress("unused")
    fun map(role: RoleToUserCross): String {
        return "crossToStr"
    }

    @Suppress("unused")
    fun mapStrToRole(roleDto: RoleDto): Role {
        val roleEntity = Role(roleDto.name)
        return roleEntity
    }
}