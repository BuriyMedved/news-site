package org.buriy.medved.frontend.feed

import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.MainLayout

@Route(value = "feed", layout = MainLayout::class)
@AnonymousAllowed
class FeedView: HorizontalLayout(), HasDynamicTitle {
    private val TITLE = "News feed"

    init{

    }

    override fun getPageTitle(): String {
        return TITLE
    }
}