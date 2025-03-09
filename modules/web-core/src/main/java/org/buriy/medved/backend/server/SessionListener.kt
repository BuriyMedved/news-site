package org.buriy.medved.backend.server

import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.SessionDestroyEvent
import org.buriy.medved.backend.dto.MessageDto
import org.slf4j.LoggerFactory
import org.buriy.medved.backend.kafka.KafkaConsumersRegistry
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class SessionListener(
    val kafkaConsumersRegistry: KafkaConsumersRegistry<MessageDto>,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SessionListener::class.java)
    }
    @EventListener
    fun logSessionInits(event: ServiceInitEvent) {
        logger.info("Регистрация слушателя закрытия сессии")
        event.source.addSessionDestroyListener { sessionDestroyEvent: SessionDestroyEvent ->
            val session = sessionDestroyEvent.session
            logger.info("Session destroyed!")
            val topics = session.getAttribute(SessionAttributes.FEED_TOPICS.value) as Set<*>

            for (topic in topics) {
                val topicID = topic as String
//                kafkaConsumersRegistry.unregister(topicID)
            }
        }
    }
}