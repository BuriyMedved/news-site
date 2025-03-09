package org.buriy.medved.frontend.feed

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.messages.MessageList
import com.vaadin.flow.component.messages.MessageListItem
import org.buriy.medved.backend.dto.MessageDto
import org.buriy.medved.backend.dto.TagDto
import org.buriy.medved.backend.kafka.MessageDtoListener
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class MessageDtoListenerImpl(
    private val currentUI: UI,
    private val list: MessageList,
    private val topicID: String,
    private val tagDtos: Set<TagDto>
): MessageDtoListener<MessageDto> {
    companion object{
        private val logger = LoggerFactory.getLogger(MessageDtoListenerImpl::class.java)
    }

    override fun onMessage(message: MessageDto) {
        if(logger.isDebugEnabled){
            logger.debug("Обработка сообщения: $message Топик: $topicID")
        }
        try {
            currentUI.access {
                val tagsList = LinkedHashSet<String>()
                message.tags.split("#").forEach {
                    tagsList.add(it)
                }
                var image: ByteArray? = null
                for (tagDto in tagDtos) {
                    if(tagsList.contains(tagDto.name)){
                        image = tagDto.image
                    }
                }

                val messageItem: MessageListItem
                if(image == null){
                    messageItem = MessageListItem(
                        message.text,
                        message.publishTime.toInstant(ZoneOffset.UTC),
                        message.tags,
                    )
                }
                else{
                    val base64Image = Base64.getEncoder().encodeToString(image)
                    messageItem = MessageListItem(
                        message.text,
                        message.publishTime.toInstant(ZoneOffset.UTC),
                        message.tags,
                        "data:image/png;base64, $base64Image"
                    )
                }

                val limit = 10
                val items = ArrayList<MessageListItem>(limit)
                val currentItems = list.items

                if(currentItems.size == limit){
                    for (i in 1 until limit) {
                        items.add(currentItems[i])
                    }
                }
                else{
                    items.addAll(currentItems)
                }
                items.add(messageItem)

                list.setItems(items)
            }
        } catch (e: Exception) {
            logger.debug("Пользовательский интерфейс не доступен ${e.message}")
        }
    }
}