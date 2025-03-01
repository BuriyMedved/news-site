package org.buriy.medved.backend.dto

import java.time.LocalDateTime
import java.util.*

data class MessageDto(
    override val id: UUID,
    var text: String,
    var publishTime: LocalDateTime,
    var tags: String
) :BaseDto(id){
}