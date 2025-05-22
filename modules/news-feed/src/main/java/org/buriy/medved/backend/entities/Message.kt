package org.buriy.medved.backend.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "z_message")
data class Message(
    @Column(name = "z_text", nullable = false, length = 2048)
    var text: String,
    @Column(name = "z_publish_time", nullable = false)
    var publishTime: LocalDateTime = LocalDateTime.now(),
    @Column(name = "z_tags", nullable = false, columnDefinition = "TEXT")
    var tags: String,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}