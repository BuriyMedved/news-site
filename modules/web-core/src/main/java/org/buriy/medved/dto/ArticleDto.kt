package org.buriy.medved.dto

import java.util.*

data class ArticleDto(
    override val id: UUID,
    val title: String,
    val text: String,
) : BaseDto(id)