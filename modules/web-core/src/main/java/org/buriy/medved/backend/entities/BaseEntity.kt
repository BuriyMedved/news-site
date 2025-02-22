package org.buriy.medved.backend.entities

import jakarta.persistence.*
import java.util.UUID

@MappedSuperclass
class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "z_id", unique = true, nullable = false, length = 36)
    var id: UUID = UUID.randomUUID()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}