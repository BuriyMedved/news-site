package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entities.Message
import org.buriy.medved.backend.entities.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, UUID>, JpaSpecificationExecutor<Tag> {
    @Query("select t.image from Tag as t where t.id = :tagId")
    fun findTagImageById(@Param(value = "tagId") tagId: UUID): ByteArray

    @Query("select t.name from Tag as t")
    fun findNames(): List<String>
}