package org.buriy.medved.backend.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.buriy.medved.backend.dto.MessageDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer


@EnableKafka
@Configuration
class KafkaConsumerConfig {
    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Bean
    fun consumerFactory(): ConsumerFactory<String, MessageDto> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "foo"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//        configProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        //если MessageDto у отправителя и получателя находятся в разных пакетах
        //или того хуже у отправителя вооюще отсутствует заголовок с типом объекта
        //то spring слушатель уходит в бесконечный цикл попыток распарсить сообщение
        //The class '<package.class>' is not in the trusted packages
        configProps[JsonDeserializer.USE_TYPE_INFO_HEADERS] = false
        configProps[JsonDeserializer.VALUE_DEFAULT_TYPE] = MessageDto::class.java.canonicalName

        val defaultKafkaConsumerFactory = DefaultKafkaConsumerFactory<String, MessageDto>(configProps)

        val jsonDeserializer = JsonDeserializer(MessageDto::class.java)
        val errorHandlingDeserializer = ErrorHandlingDeserializer(jsonDeserializer)
        defaultKafkaConsumerFactory.valueDeserializer = errorHandlingDeserializer
        
        return defaultKafkaConsumerFactory
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, MessageDto> {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, MessageDto>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

//    @KafkaListener(topics = ["test"], groupId = "foo")
//    fun listenGroupFoo(message: MessageDto) {
//        println("Received Message in group foo: $message")
//    }
}