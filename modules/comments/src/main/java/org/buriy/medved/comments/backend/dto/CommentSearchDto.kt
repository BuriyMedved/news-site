package org.buriy.medved.comments.backend.dto

import java.util.*

data class CommentSearchDto(
    var text: String?,
    var articlePtr: UUID?,
)