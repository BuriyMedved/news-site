package org.buriy.medved.backend.kafka

import org.buriy.medved.backend.dto.BaseDto
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.stereotype.Service


@Service
class KafkaListenerCreator<T : BaseDto> (
    private val kafkaListenerContainerFactory: ConcurrentKafkaListenerContainerFactory<String, T>,
){
    fun createContainer( topic: String, group: String, kafkaTemplateListener: KafkaTemplateListener<T>): ConcurrentMessageListenerContainer<String, T> {
        val container = kafkaListenerContainerFactory.createContainer(topic)
        container.containerProperties.messageListener = kafkaTemplateListener
        container.containerProperties.setGroupId(group)
        container.setBeanName(group)
        container.start()
        return container
    }
}