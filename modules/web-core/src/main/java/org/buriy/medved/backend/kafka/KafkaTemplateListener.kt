package org.buriy.medved.backend.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.buriy.medved.backend.dto.BaseDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.MessageListener

class KafkaTemplateListener<T: BaseDto>(private val registry: KafkaConsumersRegistry<T>): MessageListener<String, T> {
    companion object {
        private val logger = LoggerFactory.getLogger(KafkaTemplateListener::class.java)
    }

    override fun onMessage(record: ConsumerRecord<String, T>) {
        val topicId = record.topic()
        val listeners = registry.getListenersCopy(topicId)
        listeners.forEach { listener ->
            listener.onMessage(record.value())
        }

        if(logger.isDebugEnabled) {
            logger.debug("По топику: $topicId получено сообщение $record")
        }
    }
}