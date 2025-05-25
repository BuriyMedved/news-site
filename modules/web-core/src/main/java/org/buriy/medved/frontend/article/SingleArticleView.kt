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
import jakarta.annotation.security.RolesAllowed
import org.buriy.medved.backend.clients.CommentsClientService
import org.buriy.medved.backend.clients.UserClientService
import org.buriy.medved.backend.dto.ArticleDto
import org.buriy.medved.backend.dto.CommentDto
import org.buriy.medved.backend.dto.UserDto
import org.buriy.medved.backend.security.SecurityTools
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
@RolesAllowed("articles.read")
class SingleArticleView(
    private val articleService: ArticleService,
    private val commentsClientService: CommentsClientService,
    private val userClientService: UserClientService,
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
        val commentsDataList = Collections.synchronizedList(ArrayList<CommentDto>())
        val usersDataList = Collections.synchronizedList(ArrayList<UserDto>())

        val commentsList = MessageList()

        val currentUI = UI.getCurrent()

        val userLoadListener: (UserDto) -> Unit = { userDto ->
            usersDataList.add(userDto)
        }

        val onUsersComplete: () -> Unit = {
            try {
                val userDataMap = HashMap<UUID, UserDto>()

                usersDataList.forEach { userDto ->
                    userDataMap[userDto.id] = userDto
                }

                if(logger.isDebugEnabled){
                    logger.debug("Загружено ${usersDataList.size} пользователей")
                }

                val messageIemList = ArrayList<MessageListItem>()

                commentsDataList.forEach{commentDto ->
                    val userDto = userDataMap[commentDto.userPtr] ?: return@forEach

                    val comment = MessageListItem(
                        commentDto.text,
                        commentDto.publishTime.toInstant(ZoneOffset.UTC),
                        userDto.name
                    )
                    messageIemList.add(comment)
                }

                currentUI.access {
                    commentsList.setItems(messageIemList)
                }
            } catch (e: Exception) {
                logger.debug("Пользовательский интерфейс не доступен ${e.message}")
            }
        }

        val onCommentsComplete: () -> Unit = {
            val commentsAuthors = commentsDataList.map { commentDto ->
                commentDto.userPtr
            }.toList()

            if(logger.isDebugEnabled) {
                logger.debug("Загружено ${commentsDataList.size} комментариев. Авторов: ${commentsAuthors.size} ")
            }

            userClientService.findBulk(commentsAuthors, userLoadListener, onUsersComplete)
        }

        val commentLoadListener: (CommentDto) -> Unit = { commentDto: CommentDto ->
            commentsDataList.add(commentDto)
        }
        commentsClientService.loadComments(articleDto, commentLoadListener, onCommentsComplete)

        add(commentsList)
    }

    private fun addCommentArea(articleDto: ArticleDto) {
        val textArea = TextArea(commentButtonLabel, commentTextPlaceholder)
        textArea.setWidthFull()

        val saveCommentButton = Button()
        saveCommentButton.text = commentButtonText
        saveCommentButton.addClickListener { _ ->
            val userID = SecurityTools.getUserID()
            val commentDto = CommentDto(UUID.randomUUID(), textArea.value, LocalDateTime.now(), articleDto.id, UUID.fromString(userID))
            commentsClientService.save(commentDto) { _: Void -> println("SUCCESS") }
        }

        add(textArea, saveCommentButton)
    }

    override fun getPageTitle(): String {
        return "TEST"
    }
}