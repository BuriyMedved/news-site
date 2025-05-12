package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entities.RoleToUserCross
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleToUserCrossRepository: JpaRepository<RoleToUserCross, UUID>, JpaSpecificationExecutor<RoleToUserCross> {
    fun findByUserId(id: UUID): List<RoleToUserCross>
    fun deleteByUserIdAndRoleId(userId: UUID, roleId: UUID)
}