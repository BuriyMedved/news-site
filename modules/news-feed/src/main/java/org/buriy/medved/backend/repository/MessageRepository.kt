package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageRepository: JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message>