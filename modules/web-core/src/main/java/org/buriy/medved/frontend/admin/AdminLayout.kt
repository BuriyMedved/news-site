package org.buriy.medved.frontend.admin

import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.RoutePrefix
import com.vaadin.flow.spring.security.AuthenticationContext
import jakarta.annotation.security.RolesAllowed
import org.buriy.medved.frontend.AbstractMainLayout
import org.vaadin.lineawesome.LineAwesomeIcon

@RoutePrefix("/admin")
@RolesAllowed("admin")
class AdminLayout(
    authenticationContext : AuthenticationContext
) : AbstractMainLayout(authenticationContext ), HasDynamicTitle {
    private val title = "Администрирование"

    companion object{
        private const val USERS_LABEL = "Пользователи"
    }

    override fun createMenuItems(): Array<Tab> {
        return arrayOf(
            createTab(LineAwesomeIcon.USERS_SOLID.create(), USERS_LABEL, UserView::class.java),
        )
    }

    override fun getPageTitle(): String {
        return title
    }
}