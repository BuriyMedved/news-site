package org.buriy.medved.frontend.article

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.messages.MessageList
import com.vaadin.flow.component.messages.MessageListItem
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import org.buriy.medved.backend.clients.CommentsClientService
import org.buriy.medved.backend.dto.ArticleDto
import org.buriy.medved.backend.dto.CommentDto
import org.buriy.medved.backend.service.ArticleService
import org.buriy.medved.frontend.MainLayout
import org.buriy.medved.frontend.components.Divider
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


@Route(value = "article/:id", layout = MainLayout::class)
@CssImport(value = "./styles/components/articles-layout.css")
@CssImport(value = "./styles/components/common.css")
class SingleArticleView(
    private val articleService: ArticleService,
    private val commentsClientService: CommentsClientService
): VerticalLayout(), BeforeEnterObserver, HasDynamicTitle {

    companion object{
        private val logger = LoggerFactory.getLogger(SingleArticleView::class.java)
    }

    private lateinit var articleID: String
    private val horizontalLayout: HorizontalLayout = HorizontalLayout()
    private val commentButtonText = "Добавить комментарий"
    private val commentButtonLabel = "Ваш комментарий"
    private val commentTextPlaceholder = "Текст комментария"

    init{
        add(horizontalLayout)
    }

    override fun beforeEnter(event: BeforeEnterEvent?) {
        articleID = event!!.routeParameters.get("id").get()
        val articleDto = articleService.findById(UUID.fromString(articleID))
        if(articleDto == null) {
            return
        }

        val image = Image()
        image.src = "/api/v1/articles/image?id=${articleDto.id}"
        image.width = "50%"
        val header = H2(articleDto.title)
        header.width = "50%"

        horizontalLayout.add(image, header)

        val articlePreviewDiv = Div()
        articlePreviewDiv.text = articleDto.preview

        val articleTextDiv = Div()
        articleTextDiv.text = articleDto.text

        add(articlePreviewDiv, Divider(), articleTextDiv)

        addCommentArea(articleDto)
        add(Divider())
        addCommentsList(articleDto)
    }

    private fun addCommentsList(articleDto: ArticleDto) {
        val commentsDataList = Collections.synchronizedList(ArrayList<MessageListItem>())
        val commentsList = MessageList()

        val currentUI = UI.getCurrent()
        val onComplete: () -> Unit = {
            try {
                currentUI.access {
                    commentsList.setItems(commentsDataList)
                }
            } catch (e: Exception) {
                logger.debug("Пользовательский интерфейс не доступен ${e.message}")
            }
        }
        val function: (CommentDto) -> Unit = { commentDto: CommentDto ->
            val comment = MessageListItem(
                commentDto.text,
                commentDto.publishTime.toInstant(ZoneOffset.UTC),
                "Matt Mambo"
            )
            commentsDataList.add(comment)
        }
        commentsClientService.loadComments(articleDto, function, onComplete)

        add(commentsList)
    }

    private fun addCommentArea(articleDto: ArticleDto) {
        val textArea = TextArea(commentButtonLabel, commentTextPlaceholder)
        textArea.setWidthFull()

        val saveCommentButton = Button()
        saveCommentButton.text = commentButtonText
        saveCommentButton.addClickListener { _ ->
            val commentDto = CommentDto(UUID.randomUUID(), textArea.value, LocalDateTime.now(), articleDto.id)
            commentsClientService.save(commentDto) { _: Void -> println("SUCCESS") }
        }

        add(textArea, saveCommentButton)
    }

    override fun getPageTitle(): String {
        return "TEST"
    }
}