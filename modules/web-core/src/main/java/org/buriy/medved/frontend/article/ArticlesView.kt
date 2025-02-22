package org.buriy.medved.frontend.article

import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.MainLayout
import org.buriy.medved.service.ArticleService

@Route(value = "articles", layout = MainLayout::class)
@AnonymousAllowed
class ArticlesView(
    articleService: ArticleService
): HorizontalLayout(), HasDynamicTitle {
    private val TITLE = "Articles"

    init{
        val findAll = articleService.findAll()
        findAll.forEach { articleDto -> println(articleDto) }
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}