package org.buriy.medved.frontend.feed

import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.MainLayout

@Route(value = "hello", layout = MainLayout::class)
@PageTitle("News feed")
@AnonymousAllowed
class FeedView: HorizontalLayout() {
//    class FeedView: HorizontalLayout(), HasDynamicTitle {
    init{
//        pageT
    }

//    override fun getPageTitle(): String {
//        TODO("Not yet implemented")
//    }
}