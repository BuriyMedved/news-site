package org.buriy.medved.backend.kafka

import org.buriy.medved.backend.dto.BaseDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class KafkaConsumersRegistry<T : BaseDto>(
    private val kafkaListenerCreator: KafkaListenerCreator<T>,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(KafkaConsumersRegistry::class.java)
    }
    private val registry: MutableMap<String, MutableMap<String, MutableList<MessageDtoListener<T>>>> = HashMap()
    private val containers: MutableMap<String, ConcurrentMessageListenerContainer<String, T>> = HashMap()

    fun register(key: RegistryKey, listener: MessageDtoListener<T>) {
        if(logger.isDebugEnabled) {
            logger.debug("Регистрация нового слушателя событий для ключа $key")
        }
        synchronized(registry) {
            val topicId = key.topicId
            val userId = key.userId

            var listenersPerTopic = registry[topicId]
            var isNewTopic = false
            if(listenersPerTopic == null){
                isNewTopic = true
                listenersPerTopic = HashMap()
                registry[topicId] = listenersPerTopic
            }

            var listenersPerUser = listenersPerTopic[userId]
            if(listenersPerUser == null){
                listenersPerUser = ArrayList()
                listenersPerTopic[userId] = listenersPerUser
            }

            listenersPerUser.add(listener)

            if(isNewTopic){
                val container = kafkaListenerCreator.createContainer(topicId, "root", KafkaTemplateListener(this))
                containers[topicId] = container
            }
        }
    }

    fun unregister(key: RegistryKey, listener: MessageDtoListener<T>){
        if(logger.isDebugEnabled) {
            logger.debug("Удаление слушателя событий для ключа $key")
        }
        synchronized(registry) {
            val topicId = key.topicId
            val userId = key.userId

            val listenersPerUserMap = registry[topicId]

            if(listenersPerUserMap == null){
                return
            }

            val userListeners = listenersPerUserMap[userId]
            if(userListeners == null){
                return
            }

            userListeners.remove(listener)
            if(userListeners.isEmpty()){
                listenersPerUserMap.remove(userId)
            }

            if(listenersPerUserMap.isEmpty()){
                registry.remove(topicId)
                val oldValue = containers.remove(topicId)
                if(oldValue != null){
                    if(logger.isDebugEnabled) {
                        logger.debug("Не осталось слушателей для топика $topicId. Останавливаю контейнер")
                    }
                    oldValue.stop()
                }
            }
        }
    }

    fun getListenersCopy(topicID: String): MutableList<MessageDtoListener<T>>{
        synchronized(registry) {

            val listeners = registry[topicID]
            if(listeners == null){
                return Collections.emptyList()
            }
            val copy = ArrayList<MessageDtoListener<T>>()

            listeners.forEach { entry ->
                entry.value.forEach {
                    copy.add(it)
                }
            }
            return copy
        }
    }
}