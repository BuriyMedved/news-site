package org.buriy.medved.backend.service

import org.buriy.medved.backend.dto.TagDto
import org.buriy.medved.backend.mapper.TagMapper
import org.buriy.medved.backend.repository.TagRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TagService (
    private val tagRepository: TagRepository,
    private val tagMapper: TagMapper,
){
    fun findAll(): List<TagDto>{
        return tagRepository.findAll().map { entity -> tagMapper.toDto(entity)}
    }

    fun findTagImageById(id: UUID): ByteArray{
        return tagRepository.findTagImageById(id)
    }

    fun findNames(): List<String>{
        return tagRepository.findNames()
    }
}