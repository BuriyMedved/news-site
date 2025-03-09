package org.buriy.medved.comments.backend.repository

import org.buriy.medved.comments.backend.entities.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository: JpaRepository<Comment, UUID>, JpaSpecificationExecutor<Comment>