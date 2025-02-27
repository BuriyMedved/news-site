package org.buriy.medved.comments.backend.dto

import java.time.LocalDateTime
import java.util.*

data class CommentSearchDto(
    var text: String?,
//    var publishTime: LocalDateTime?,
    var articlePtr: UUID?,
)