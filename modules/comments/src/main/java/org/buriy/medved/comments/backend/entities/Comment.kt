package org.buriy.medved.comments.backend.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "z_article")
data class Comment(
    @Column(name = "z_text", nullable = false, length = 2048)
    var text: String,
    @Column(name = "z_publish_time", nullable = false)
    var publishTime: LocalDateTime = LocalDateTime.now(),
    @Column(name = "z_article_ptr", unique = false, nullable = false, length = 36)
    var articlePtr: UUID
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}