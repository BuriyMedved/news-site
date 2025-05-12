package org.buriy.medved.backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "z_role_to_user_cross")
data class RoleToUserCross(
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "z_user_ptr", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "z_role_ptr", nullable = false)
    val role: Role,
) : BaseEntity(){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}