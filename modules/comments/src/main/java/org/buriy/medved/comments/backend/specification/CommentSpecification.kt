package org.buriy.medved.comments.backend.specification

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.buriy.medved.comments.backend.dto.CommentSearchDto
import org.buriy.medved.comments.backend.entities.Comment
import org.springframework.data.jpa.domain.Specification

class CommentSpecification(val searchDto: CommentSearchDto): Specification<Comment> {
    override fun toPredicate(
        root: Root<Comment>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()
        if(searchDto.text != null && searchDto.text != "") {
            predicates.add(criteriaBuilder.like(root.get("text"), "%${searchDto.text}%"))
        }

        if(searchDto.articlePtr != null) {
            predicates.add(criteriaBuilder.equal(root.get<String>("articlePtr"), searchDto.articlePtr))
        }

        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}