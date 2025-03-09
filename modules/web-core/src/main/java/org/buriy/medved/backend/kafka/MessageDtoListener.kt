package org.buriy.medved.backend.kafka

import org.buriy.medved.backend.dto.BaseDto

interface MessageDtoListener<in T : BaseDto> {
    fun onMessage(message: T)
}