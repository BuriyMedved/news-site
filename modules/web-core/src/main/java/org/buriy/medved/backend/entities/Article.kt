package org.buriy.medved.backend.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "z_article")
data class Article (
    @Column(name = "z_title", nullable = false, length = 512)
    var title: String,
    @Column(name = "z_preview", nullable = false, length = 1024)
    var preview: String,
    @Column(name = "z_text", nullable = false, columnDefinition = "TEXT")
    var text: String,
    @Column(name = "z_image", nullable = true)
    var image: ByteArray
): BaseEntity() {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}