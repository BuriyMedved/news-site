package org.buriy.medved.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "z_article")
data class Article (
    @Column(name = "z_title", nullable = false, length = 512)
    val title: String,
    @Column(name = "z_text", nullable = false, columnDefinition = "TEXT")
    val text: String
): BaseEntity()