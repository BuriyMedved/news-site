package org.buriy.medved.comments.backend.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.buriy.medved.backend.entities.BaseEntity
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "z_comment")
data class Comment(
    @Column(name = "z_text", nullable = false, length = 2048)
    var text: String,
    @Column(name = "z_publish_time", nullable = false)
    var publishTime: LocalDateTime = LocalDateTime.now(),
    @Column(name = "z_article_ptr", unique = false, nullable = false, length = 36)
    var articlePtr: UUID,
    @Column(name = "user_ptr", unique = false, nullable = false, length = 36)
    var userPtr: UUID
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}