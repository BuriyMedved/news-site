package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entities.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository: JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    fun findByName(name: String): Optional<Role>
}