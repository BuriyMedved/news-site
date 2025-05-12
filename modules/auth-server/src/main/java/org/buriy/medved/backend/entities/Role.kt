package org.buriy.medved.backend.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "z_role")
data class Role(
    @Column(name = "z_name", nullable = false, length = 2048)
    var name: String,
) : BaseEntity(){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}