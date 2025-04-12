package org.buriy.medved.comments.backend.service

import jakarta.transaction.Transactional
import org.buriy.medved.comments.backend.dto.CommentDto
import org.buriy.medved.comments.backend.dto.CommentSearchDto
import org.buriy.medved.comments.backend.mapper.CommentMapper
import org.buriy.medved.comments.backend.repository.CommentRepository
import org.buriy.medved.comments.backend.specification.CommentSpecification
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class CommentService(
    val commentRepository: CommentRepository,
    val commentMapper: CommentMapper,
) {
//    @Transactional
    fun save(commentDto: CommentDto) {
        commentRepository.save(commentMapper.toEntity(commentDto))
    }

    fun findAllForArticle(commentSearchDto: CommentSearchDto): List<CommentDto> {
        val searchSpec = CommentSpecification(commentSearchDto)
        val sort = Sort.by(Sort.Direction.DESC, "publishTime")
        return commentRepository.findAll(searchSpec, sort).map { comment -> commentMapper.toDto(comment) }
    }

    fun findAllForArticleCount(commentSearchDto: CommentSearchDto): Long {
        val searchSpec = CommentSpecification(commentSearchDto)
        return commentRepository.count(searchSpec)
    }
}