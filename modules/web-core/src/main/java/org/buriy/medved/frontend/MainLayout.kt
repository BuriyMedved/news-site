package org.buriy.medved.frontend

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.tabs.TabsVariant
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.article.ArticlesView
import org.buriy.medved.frontend.feed.FeedView
import org.vaadin.lineawesome.LineAwesomeIcon
import java.util.*


@Route("")
@CssImport("./styles/components/main-layout.css")//Stylesheet
@CssImport(value = "./styles/components/app-layout.css", themeFor = "vaadin-app-layout")
@CssImport(value = "./styles/components/tab.css", themeFor = "vaadin-tab")
@AnonymousAllowed
class MainLayout : AppLayout(), HasDynamicTitle {
    private val menu: Tabs
    private var viewTitle: H1? = null
    private val TITLE = "Новости бизнеса"
    private val FEED_LABEL = "Лента"
    private val ARTICLE_LABEL = "Статьи"
    //Панорама 2.0
    init{
        // Use the drawer for the menu
        setPrimarySection(Section.DRAWER);

        // Make the nav bar a header
        addToNavbar(true, createHeaderContent());

        // Put the menu in the drawer
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private fun createHeaderContent(): Component {
        val layout = HorizontalLayout()

        // Configure styling for the header
        layout.setId("header")
        layout.themeList["dark"] = true
        layout.setWidthFull()
        layout.isSpacing = false
        layout.isPadding = true
        layout.alignItems = FlexComponent.Alignment.CENTER
        layout.justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN;

        // Have the drawer toggle button on the left
        layout.add(DrawerToggle())

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        viewTitle = H1()
        layout.add(viewTitle)

        // A user icon
//        layout.add(Image("img/user.svg", "Avatar"))

        layout.add(LineAwesomeIcon.USER_SOLID.create())
        return layout
    }

    private fun createDrawerContent(menu: Tabs): Component {
        val drawerLayout = VerticalLayout()

        // Configure styling for the drawer
        drawerLayout.setSizeFull()
        drawerLayout.isPadding = false
        drawerLayout.isSpacing = false
        drawerLayout.themeList["spacing-s"] = true
        drawerLayout.alignItems = FlexComponent.Alignment.STRETCH

        val mainLogo = Image("img/logo-business.png", "Logo")
        mainLogo.setId("main-logo")

        // Have a drawer header with an application logo
        val logoLayout = HorizontalLayout()
        logoLayout.setId("logo")
        logoLayout.alignItems = FlexComponent.Alignment.CENTER
        logoLayout.add(mainLogo)
//        logoLayout.add(H1("My Project"))

        // Display the logo and the menu in the drawer
        drawerLayout.add(logoLayout, menu)
        return drawerLayout
    }

    private fun createMenu(): Tabs {
        val tabs = Tabs()
        tabs.orientation = Tabs.Orientation.VERTICAL
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL)
        tabs.setId("tabs")
        tabs.add(*createMenuItems())
        return tabs
    }

    private fun createMenuItems(): Array<Tab> {
        return arrayOf(
            createTab(FEED_LABEL, FeedView::class.java),
            createTab(ARTICLE_LABEL, ArticlesView::class.java),
        )
    }

    private fun createTab(
        text: String,
        navigationTarget: Class<out Component>
    ): Tab {
        val tab = Tab()
        tab.add(RouterLink(text, navigationTarget))
        ComponentUtil.setData(tab, Class::class.java, navigationTarget)
        return tab
    }

    override fun afterNavigation() {
        super.afterNavigation()

        // Select the tab corresponding to currently shown view
        getTabForComponent(content).ifPresent { selectedTab: Tab? ->
            menu.selectedTab = selectedTab
        }

        // Set the view title in the header
        viewTitle!!.text = getCurrentPageTitle()
    }

    private fun getTabForComponent(component: Component): Optional<Tab> {
        return menu.children
            .filter { tab: Component? ->
                ComponentUtil.getData(tab, Class::class.java) == component.javaClass
            }
            .findFirst().map { obj: Component? ->
                Tab::class.java.cast(obj)
            }
    }

    private fun getCurrentPageTitle(): String {
        return (content as HasDynamicTitle).pageTitle
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}