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
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.buriy.medved.frontend.feed.FeedView
import java.util.*


@Route("")
@PageTitle("News")
@CssImport("./css/main-layout.css")//Stylesheet
@AnonymousAllowed
class MainLayout : AppLayout(){
    private val menu: Tabs
    private var viewTitle: H1? = null
    private val FEED_LABEL = "Новости"
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
        layout.alignItems = FlexComponent.Alignment.CENTER

        // Have the drawer toggle button on the left
        layout.add(DrawerToggle())

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        viewTitle = H1()
        layout.add(viewTitle)

        // A user icon
        layout.add(Image("img/user.svg", "Avatar"))

        return layout
    }

    private fun createDrawerContent(menu: Tabs): Component {
        val layout = VerticalLayout()

        // Configure styling for the drawer
        layout.setSizeFull()
        layout.isPadding = false
        layout.isSpacing = false
        layout.themeList["spacing-s"] = true
        layout.alignItems = FlexComponent.Alignment.STRETCH

        // Have a drawer header with an application logo
        val logoLayout = HorizontalLayout()
        logoLayout.setId("logo")
        logoLayout.alignItems = FlexComponent.Alignment.CENTER
        val mainLogo = Image("img/logo.png", "Logo")
        mainLogo.setId("main-logo")
        logoLayout.add(mainLogo)
        logoLayout.add(H1("My Project"))

        // Display the logo and the menu in the drawer
        layout.add(logoLayout, menu)
        return layout
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
            createTab(FEED_LABEL, FeedView::class.java)
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
        return content.javaClass.getAnnotation(PageTitle::class.java).value
    }
}