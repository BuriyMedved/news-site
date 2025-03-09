package org.buriy.medved.frontend.feed

import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.listbox.MultiSelectListBox
import com.vaadin.flow.component.messages.MessageList
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.backend.clients.TagsClientService
import org.buriy.medved.backend.common.imageBytesToBase64String
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
    private val title = "Лента новостей"
    private val tagsTitle = "Тэги новостей"
    private val messagesTitleText = "Новости"

    companion object {
        private val logger = LoggerFactory.getLogger(FeedView::class.java)
    }

    private val listeners: ConcurrentHashMap<RegistryKey, MessageDtoListenerImpl> = ConcurrentHashMap()
    private val concurrentHashMap = ConcurrentHashMap<UUID, TagDto>()

    init{
        val currentUI = UI.getCurrent()
        val userID = currentUI.session.session.id as String
        if(logger.isDebugEnabled){
            logger.debug("Компонент ленты новостей создан для сессии с идентификатором $userID")
        }
        val list = MessageList()
        val tagSelector: MultiSelectListBox<TagDto> = MultiSelectListBox<TagDto>()
        tagSelector.setRenderer(ComponentRenderer { tagDto: TagDto ->
            val row = HorizontalLayout()
            row.alignItems = FlexComponent.Alignment.CENTER

            val avatar = Avatar()
            avatar.name = tagDto.name
            avatar.image = imageBytesToBase64String(tagDto.image)

            val name = Span(tagDto.name)

            val column = VerticalLayout()
            column.add(name)
            column.isPadding = false
            column.isSpacing = false

            row.add(avatar, column)
            row.style["line-height"] = "var(--lumo-line-height-m)"
            row
        })

//        tagSelector.setItemLabelGenerator(TagDto::name)
        tagSelector.addValueChangeListener { event ->
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

        val function = { tagDto: TagDto ->
            concurrentHashMap[tagDto.id] = tagDto
        }

        val onComplete = Runnable {
            if(logger.isDebugEnabled) {
                if(concurrentHashMap.isNotEmpty()) {
                    logger.debug("Получен список тегов")
                    concurrentHashMap.forEach { entry -> logger.debug("${entry.value}") }
                }
            }
            try {
                currentUI.access {
                    val values = concurrentHashMap.values
                    tagSelector.setItems(values)
                    tagSelector.select(values)
                }
            } catch (e: Exception) {
                logger.debug("Пользовательский интерфейс не доступен ${e.message}")
            }
        }
        tagsClientService.loadTags (function, onComplete)

        val tagsSelectorTitle = H4(tagsTitle)
        val messagesTitle = H4(messagesTitleText)

        val selectorLayout = VerticalLayout()
        selectorLayout.add(tagsSelectorTitle, tagSelector)
        selectorLayout.setHeightFull()

        val messagesLayout = VerticalLayout()
        messagesLayout.add(messagesTitle, list)
        messagesLayout.setSizeFull()

        val rootLayout = HorizontalLayout()
        
        rootLayout.setSizeFull()
        rootLayout.add(selectorLayout, messagesLayout)

        add(rootLayout)
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

    private fun cleanListeners() {
        for (entry in listeners) {
            val key = entry.key
            kafkaConsumersRegistry.unregister(key, entry.value)
        }
        listeners.clear()
    }

    override fun getPageTitle(): String {
        return title
    }

    override fun onDetach(detachEvent: DetachEvent) {
        if(logger.isDebugEnabled) {
            logger.debug("Компонент выгружен. Удаляю всех слушателей. Всего слушателей: ${listeners.size}")
        }
        cleanListeners()
    }

}