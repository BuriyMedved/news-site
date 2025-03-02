package org.buriy.medved.backend.kafka

import org.springframework.stereotype.Service

@Service
class KafkaConsumersRegistry(
    private val kafkaListenerControlService: KafkaListenerControlService
) {
    private val registry: MutableMap<String, Long> = HashMap()

    fun register(topicId: String){
        synchronized(registry) {
            val count = registry[topicId]
            if(count == null){
                registry[topicId] = 1
                kafkaListenerControlService.startListener(topicId)
            }
            else{
                registry[topicId] = count.inc()
            }
        }
    }

    fun unregister(topicId: String){
        synchronized(registry) {
            val count = registry[topicId]
            if(count == null){
                return
            }
            else if (count == 1L){
                registry.remove(topicId)
                kafkaListenerControlService.stopListener(topicId)
            }
            else{
                registry[topicId] = count.dec()
            }
        }
    }
}