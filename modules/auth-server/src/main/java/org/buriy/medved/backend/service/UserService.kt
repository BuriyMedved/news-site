package org.buriy.medved.backend.service

import jakarta.transaction.Transactional
import org.buriy.medved.backend.dto.UserDto
import org.buriy.medved.backend.entities.Role
import org.buriy.medved.backend.entities.RoleToUserCross
import org.buriy.medved.backend.mapper.UserMapper
import org.buriy.medved.backend.repository.RoleRepository
import org.buriy.medved.backend.repository.RoleToUserCrossRepository
import org.buriy.medved.backend.repository.UserRepository
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val roleToUserCrossRepository: RoleToUserCrossRepository,
    private val userMapper: UserMapper,
) {
    fun findById(id: UUID): Optional<UserDto> {
        val userOptional = userRepository.findById(id)
        if(userOptional.isEmpty) {
            return Optional.empty<UserDto>()
        }

        val userDto = userMapper.toDto(userOptional.get())

        val existingRolesSet = getRolesNamesSet(userDto)
        userDto.roles = existingRolesSet

        return Optional.of(userDto)
    }

    @Transactional
    fun findAll(withPassword: Boolean = false): List<UserDto> {
        return userRepository.findAll().map { user ->
            val userDto = if(withPassword)
            {
                userMapper.toDtoWithPassword(user)
            }
            else{
                userMapper.toDto(user)
            }

            val existingRolesSet = getRolesNamesSet(userDto)
            userDto.roles = existingRolesSet

            userDto
        }
    }

    fun delete(id: UUID) {
        userRepository.deleteById(id)
    }

    fun save(userDto: UserDto): UserDto {
        val userID = UUID.randomUUID()
        userDto.id = userID
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        userDto.password = encoder.encode(userDto.password)
        val userEntity = userMapper.toEntity(userDto)

        val crossList = ArrayList<RoleToUserCross>()
        userDto.roles.forEach { role ->
            val optional = roleRepository.findByName(role)
            if(optional.isPresent){
                val roleEntity = optional.get()
                val cross = RoleToUserCross(userEntity, roleEntity)
                crossList.add(cross)
            }
        }

        userRepository.save(userEntity)

        for (roleToUserCross in crossList) {
            roleToUserCrossRepository.save(roleToUserCross)
        }

        val userOptional = userRepository.findById(userDto.id)
        return userMapper.toDto(userOptional.get())
    }

    @Transactional
    fun update(userDto: UserDto): Optional<UserDto> {
        val userEntityOptional = userRepository.findById(userDto.id)
        if(userEntityOptional.isEmpty){
            return Optional.empty()
        }

        val userEntity = userEntityOptional.get()
        if(userDto.password.isEmpty()){
            userDto.password = userEntity.password
        }
        else{
            val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
            userDto.password = encoder.encode(userDto.password)
        }

        userMapper.updateEntityFromDto(userDto, userEntity)

        userRepository.save(userEntity)

        val newRolesCrossList = ArrayList<RoleToUserCross>()
        val receivedRolesSet = userDto.roles.toSet()

        val existingRolesSet = getRolesNamesSet(userDto)

        userDto.roles.forEach { roleName ->
            if(!existingRolesSet.contains(roleName)){
                val optional = roleRepository.findByName(roleName)
                if(optional.isPresent){
                    val roleEntity = optional.get()

                    val cross = RoleToUserCross(userEntity, roleEntity)
                    newRolesCrossList.add(cross)
                }
            }
        }

        for (roleName in existingRolesSet) {
            if(!receivedRolesSet.contains(roleName)){
                val optional = roleRepository.findByName(roleName)
                if(optional.isPresent) {
                    val roleEntity = optional.get()
                    roleToUserCrossRepository.deleteByUserIdAndRoleId(userDto.id, roleEntity.id)
                }
            }
        }

        for (roleToUserCross in newRolesCrossList) {
            roleToUserCrossRepository.save(roleToUserCross)
        }

        val userOptional = userRepository.findById(userDto.id)
        return Optional.of(userMapper.toDto(userOptional.get()))
    }

    private fun getRolesNamesSet(userDto: UserDto): MutableSet<String> {
        val existingRolesList = roleToUserCrossRepository.findByUserId(userDto.id)
        val existingRolesSet = existingRolesList.stream()
            .map { roleToUserCross -> roleToUserCross.role.name }
            .collect(Collectors.toSet())
        return existingRolesSet
    }
}