package org.buriy.medved.frontend.feed

import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.messages.MessageList
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.backend.clients.TagsClientService
import org.buriy.medved.backend.dto.MessageDto
import org.buriy.medved.backend.dto.TagDto
import org.buriy.medved.backend.kafka.KafkaConsumersRegistry
import org.buriy.medved.backend.kafka.RegistryKey
import org.buriy.medved.backend.kafka.TopicList
import org.buriy.medved.frontend.MainLayout
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@Route(value = "feed", layout = MainLayout::class)
@AnonymousAllowed
class FeedView(
    tagsClientService: TagsClientService,
    private val kafkaConsumersRegistry: KafkaConsumersRegistry<MessageDto>
): HorizontalLayout(), HasDynamicTitle{
    private val TITLE = "Лента новостей"
    private val TAGS_TITLE = "Тэги новостей"

    companion object {
        private val logger = LoggerFactory.getLogger(FeedView::class.java)
    }

    private val listeners: ConcurrentHashMap<RegistryKey, MessageDtoListenerImpl> = ConcurrentHashMap()
    private val concurrentHashMap = ConcurrentHashMap<UUID, TagDto>()

    init{
        val currentUI = UI.getCurrent()
        val userID = currentUI.session.session.id as String

        val list = MessageList()
        val comboBox: MultiSelectComboBox<TagDto> = MultiSelectComboBox<TagDto>(
            TAGS_TITLE
        )

        //https://vaadin.com/docs/latest/components/combo-box#custom-item-presentation
        comboBox.setItemLabelGenerator(TagDto::name)
        comboBox.autoExpand = MultiSelectComboBox.AutoExpandMode.BOTH
        comboBox.addValueChangeListener { event ->
            val newValues = event.value
            val oldValues = event.oldValue

            for (tagDto in oldValues) {
                if(!newValues.contains(tagDto)) {
                    removeListener(tagDto.id.toString(), userID)
                }
            }

            for (tagDto in newValues) {
                if(!oldValues.contains(tagDto)) {
                    registerListener(tagDto.id.toString(), userID, currentUI, list)
                }
            }
        }
        add(comboBox)

        val function = { tagDto: TagDto ->
            concurrentHashMap[tagDto.id] = tagDto
        }

        val onComplete = Runnable {
            if(logger.isDebugEnabled) {
                if(concurrentHashMap.isNotEmpty()) {
                    logger.debug("Получен список тегов:")
                    concurrentHashMap.forEach { entry -> logger.debug("${entry.value}") }
                }
            }
            try {
                currentUI.access {
                    val values = concurrentHashMap.values
                    comboBox.setItems(values)
                    comboBox.select(values)
                }
            } catch (e: Exception) {
                logger.debug("Пользовательский интерфейс не доступен ${e.message}")
            }
        }
        tagsClientService.loadTags (function, onComplete)
//        val message2 = MessageListItem(
//            "All good. Ship it.",
//            fiftyMinsAgo, "Linsey Listy", "${tagsClientService.imageURL}$"
//        )
//        message2.userColorIndex = 2

        add(list)
    }

    private fun registerListener(
        tagID: String,
        userID: String,
        currentUI: UI,
        list: MessageList
    ) {
        val key = RegistryKey("${TopicList.TAGS_PREFIX.topic}$tagID", userID)
        val listener = MessageDtoListenerImpl(currentUI, list, tagID, HashSet(concurrentHashMap.values))

        listeners[key] = listener
        try {
            kafkaConsumersRegistry.register(key, listener)
        } catch (e: Exception) {
            logger.error("", e)
        }
    }

    private fun removeListener(tagID: String, userID: String) {
        val key = RegistryKey("${TopicList.TAGS_PREFIX.topic}$tagID", userID)
        val remove = listeners.remove(key)
        if(remove == null) {
            return
        }
        kafkaConsumersRegistry.unregister(key, remove)
    }

    override fun getPageTitle(): String {
        return TITLE
    }

    override fun onDetach(detachEvent: DetachEvent) {
        if(logger.isDebugEnabled) {
            logger.debug("Компонент выгружен. Удаляю всех слушателей")
        }
        for (entry in listeners) {
            val key = entry.key
            removeListener(key.topicId, key.userId)
        }

    }
}