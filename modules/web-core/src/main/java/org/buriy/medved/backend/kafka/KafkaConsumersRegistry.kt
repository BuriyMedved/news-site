package org.buriy.medved.backend.kafka

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

enum class KafkaConsumersRegistry {
    INSTANCE;

    private val registry: ConcurrentMap<String, Long> = ConcurrentHashMap()

    fun register(topicId: String){
        registry.merge(topicId, 1, Long::plus)
    }

    fun unregister(topicId: String){
        registry.computeIfPresent(topicId) { key, value ->
            if(value == 1L) null else value.dec()
        }
    }
}