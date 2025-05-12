package org.buriy.medved.frontend

import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext
import org.buriy.medved.frontend.article.ArticlesView
import org.buriy.medved.frontend.feed.FeedView
import org.vaadin.lineawesome.LineAwesomeIcon

@AnonymousAllowed
class MainLayout(
    authenticationContext : AuthenticationContext
) : AbstractMainLayout(authenticationContext), HasDynamicTitle {
    companion object{
        private const val TITLE = "Новости бизнеса"
        private const val FEED_LABEL = "Лента"
        private const val ARTICLE_VIEW = "Статьи"
    }

    override fun createMenuItems(): Array<Tab> {
        return arrayOf(
            createTab(LineAwesomeIcon.RSS_SOLID.create(), FEED_LABEL, FeedView::class.java),
            createTab(LineAwesomeIcon.NEWSPAPER.create(), ARTICLE_VIEW, ArticlesView::class.java),
        )
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}