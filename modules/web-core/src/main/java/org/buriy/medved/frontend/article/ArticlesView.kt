package org.buriy.medved.frontend.article

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.MainLayout
import org.buriy.medved.backend.service.ArticleService

@Route(value = "articles", layout = MainLayout::class)
@CssImport(value = "./styles/components/articles-layout.css")
@AnonymousAllowed
class ArticlesView(
    articleService: ArticleService
): HorizontalLayout(), HasDynamicTitle {
    private val TITLE = "Статьи"

    init{
        val articleDtoList = articleService.findAll()

        val rootLayout = VerticalLayout()
        rootLayout.setSizeFull()
        rootLayout.alignItems = FlexComponent.Alignment.START
        if(articleDtoList.isNotEmpty()){
            var i = 0
            var horizontalLayout: HorizontalLayout? = null
            articleDtoList.forEach { articleDto ->
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

                    articleLayout.add(header, preview, image)
                    rootLayout.add(articleLayout)
                }
                else{
                    val header = H3(articleDto.title)
                    image.className = "gridImage"
                    articleLayout.add(image, header)
//                    articleLayout.width = "5%"
                    if((i - 1) % 3 == 0){
                        horizontalLayout = HorizontalLayout()
//                        horizontalLayout!!.setSizeFull()
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
        return TITLE
    }
}