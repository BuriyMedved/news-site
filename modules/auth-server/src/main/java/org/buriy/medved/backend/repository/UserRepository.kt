package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    fun findUserByIdIsIn(ids: Collection<UUID>): List<User>
    fun findByLogin(login: String): Optional<User>
}