package org.buriy.medved.backend.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "z_tag")
data class Tag(
    @Column(name = "z_name", nullable = false)
    val name: String,
    @Column(name = "z_image", nullable = false)
    val image: ByteArray
) :BaseEntity() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}