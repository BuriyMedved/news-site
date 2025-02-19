package org.buriy.medved.frontend.article

import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.MainLayout

@Route(value = "articles", layout = MainLayout::class)
@AnonymousAllowed
class ArticlesView: HorizontalLayout(), HasDynamicTitle {
    private val TITLE = "Articles"

    init{

    }

    override fun getPageTitle(): String {
        return TITLE
    }
}