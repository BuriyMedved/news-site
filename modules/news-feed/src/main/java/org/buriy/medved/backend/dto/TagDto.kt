package org.buriy.medved.backend.dto

import java.util.*

data class TagDto(
    override val id: UUID,
    val name: String,
    val image: ByteArray,
): BaseDto(id) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}