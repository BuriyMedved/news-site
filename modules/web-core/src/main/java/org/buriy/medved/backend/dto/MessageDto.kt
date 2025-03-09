package org.buriy.medved.backend.dto

import org.buriy.medved.backend.common.NoArg
import java.time.LocalDateTime
import java.util.*

@NoArg
data class MessageDto(
    override val id: UUID,
    var text: String,
    var publishTime: LocalDateTime,
    var tags: String
) :BaseDto(id){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}