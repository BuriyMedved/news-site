package org.buriy.medved.backend.kafka

import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.stereotype.Service


@Service
class KafkaListenerControlService(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    val registry: KafkaListenerEndpointRegistry
) {

    
    fun startListener(listenerId: String) {
        val listenerContainer = registry.getListenerContainer(listenerId)
        if (listenerContainer != null && !listenerContainer.isRunning) {
            listenerContainer.start()
        }
    }

    fun stopListener(listenerId: String) {
        val listenerContainer = registry.getListenerContainer(listenerId)
        if (listenerContainer != null && listenerContainer.isRunning) {
            listenerContainer.stop()
        }
    }
}