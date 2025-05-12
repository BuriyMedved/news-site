package org.buriy.medved.backend.dto

import org.buriy.medved.backend.annotation.NoArg
import java.util.*

@NoArg
data class UserDto(
    override var id: UUID,
    var login: String,
    var password: String,
    var name: String,
    var email: String,
    var roles: MutableSet<String>,
) : BaseDto(id){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}