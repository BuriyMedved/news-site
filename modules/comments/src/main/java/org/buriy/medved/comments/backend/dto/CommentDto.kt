package org.buriy.medved.comments.backend.dto

import org.buriy.medved.backend.dto.BaseDto
import java.time.LocalDateTime
import java.util.*

data class CommentDto(
    override val id: UUID,
    var text: String,
    var publishTime: LocalDateTime,
    var articlePtr: UUID,
): BaseDto(id) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}