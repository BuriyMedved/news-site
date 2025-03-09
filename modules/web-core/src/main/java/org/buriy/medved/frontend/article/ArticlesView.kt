package org.buriy.medved.frontend.article

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.backend.clients.CommentsClientService
import org.buriy.medved.backend.service.ArticleService
import org.buriy.medved.frontend.MainLayout
import org.slf4j.LoggerFactory
import org.vaadin.lineawesome.LineAwesomeIcon
import java.time.format.DateTimeFormatter

@Route(value = "articles", layout = MainLayout::class)
@CssImport(value = "./styles/components/articles-layout.css")
@AnonymousAllowed
class ArticlesView(
    articleService: ArticleService,
    commentsClientService: CommentsClientService,
): HorizontalLayout(), HasDynamicTitle {
    private val title = "Статьи"
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    companion object {
        private val logger = LoggerFactory.getLogger(ArticlesView::class.java)
    }
    init{
        val articleDtoList = articleService.findAll()

        val rootLayout = VerticalLayout()
        rootLayout.setSizeFull()
        rootLayout.alignItems = FlexComponent.Alignment.START
        if(articleDtoList.isNotEmpty()){
            var i = 0
            var horizontalLayout: HorizontalLayout? = null
            val currentUI = UI.getCurrent()
            articleDtoList.forEach { articleDto ->
                val footer = HorizontalLayout()
                val commentsCounter = Span("...")
                try {
                    val function: (t: Long) -> Unit = { commentsCount ->
                        try {
                            currentUI.access { commentsCounter.text = "$commentsCount" }
                        } catch (e: Exception) {
                            logger.debug("Пользовательский интерфейс не доступен ${e.message}")
                        }
                    }
                    commentsClientService.loadCommentsCounter(articleDto, function)
                } catch (e: Exception) {
                    logger.debug("Сервис комментариев не доступен", e)
                }
                val publishTime = Span(articleDto.publishTime.format(dateFormat))

                footer.add(commentsCounter)
                footer.add(LineAwesomeIcon.COMMENTS.create())
                footer.add(publishTime)

                val image = Image()
                image.src = "/articles/image?id=${articleDto.id}"

                val articleLayout = VerticalLayout()
                articleLayout.alignItems = FlexComponent.Alignment.START
                if(i == 0){
                    val header = H1(articleDto.title)
                    val preview = Div()
                    preview.text = articleDto.preview

                    preview.setSizeFull()
                    articleLayout.setSizeFull()
                    image.setSizeFull()

                    articleLayout.add(header, preview, image, footer)
                    rootLayout.add(articleLayout)
                }
                else{
                    val header = H3(articleDto.title)
                    image.className = "gridImage"
                    articleLayout.add(image, header, footer)

                    if((i - 1) % 3 == 0){
                        horizontalLayout = HorizontalLayout()
                        rootLayout.add(horizontalLayout)
                    }

                    horizontalLayout!!.alignItems = FlexComponent.Alignment.CENTER
                    horizontalLayout!!.justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                    horizontalLayout!!.add(articleLayout)
                }

                i++
            }
        }

        add(rootLayout)
    }

    override fun getPageTitle(): String {
        return title
    }
}