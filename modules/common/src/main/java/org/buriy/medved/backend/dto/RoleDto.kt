package org.buriy.medved.backend.dto

import org.buriy.medved.backend.annotation.NoArg
import java.util.*

@NoArg
data class RoleDto(
    override val id: UUID,
    val name: String,
    val description: String
): BaseDto(id) {
}