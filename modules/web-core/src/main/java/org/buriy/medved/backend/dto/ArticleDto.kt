package org.buriy.medved.backend.dto

import java.util.*

data class ArticleDto(
    override val id: UUID,
    val title: String,
    val text: String,
    val preview: String,
    val image: ByteArray?,
) : BaseDto(id) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}