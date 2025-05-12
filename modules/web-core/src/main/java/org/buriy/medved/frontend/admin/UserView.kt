package org.buriy.medved.frontend.admin

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.dataview.GridListDataView
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.RolesAllowed
import org.buriy.medved.backend.clients.UserClientService
import org.buriy.medved.backend.dto.UserDto
import org.slf4j.LoggerFactory
import java.util.*

@Route(value = "users", layout = AdminLayout::class)
@CssImport(value = "./styles/components/common.css")
@RolesAllowed("admin")
class UserView(
    private val userClientService: UserClientService
): VerticalLayout(), HasDynamicTitle {

    companion object {
        private val logger = LoggerFactory.getLogger(UserView::class.java)

        private const val TITLE = "Пользователи"
        private const val ADD_LABEL = "Добавить"
        private const val EDIT_LABEL = "Редактировать"
        private const val DELETE_LABEL = "Удалить"
    }
    init{
        val currentUI = UI.getCurrent()

        val userGrid = UserGrid()

        var listDataView: GridListDataView<UserDto>? = null
        val userList: MutableList<UserDto> = ArrayList()
        val onComplete = Runnable {
            try {
                currentUI.access {
                    listDataView = userGrid.setItems(userList)
                }
            } catch (e: Exception) {
                logger.debug("Пользовательский интерфейс не доступен ${e.message}")
            }
        }

        val loadListener: (userDto: UserDto) -> Unit =
            { userDto: UserDto ->
                userList.add(userDto)
            }

        userClientService.findAll(loadListener, onComplete)

        add(userGrid)

        val addButton = Button(ADD_LABEL)

        addButton.addClickListener {
            val userDtoTemplate = UserDto(
                UUID.randomUUID(),
                "",
                "",
                "",
                "",
                Collections.emptySet()
            )
            val userDialog = UserDialog(userDtoTemplate, true){
                userClientService.save(userDtoTemplate,
                    { userDto: UserDto ->
                        try {
                            currentUI.access {
                                listDataView?.addItem(userDto)
                            }
                        } catch (e: Exception) {
                            logger.debug("Пользовательский интерфейс не доступен ${e.message}")
                        }
                    }
                )
            }
            userDialog.open()
        }

        val editButton = Button(EDIT_LABEL)

        editButton.addClickListener {
            if(userGrid.selectedItems.isEmpty()){
                return@addClickListener
            }

            val selectedDto = userGrid.selectedItems.first()
            val userDialog = UserDialog(selectedDto, false){
                userClientService.update(selectedDto,
                    { userDto: UserDto ->
                        try {
                            currentUI.access {
                                //приходится сбрасывать пароль потому что selectedItems
                                //прододжает содержать старое значение даже после refreshItem
                                selectedDto.password = ""
                                userGrid.dataProvider.refreshItem(userDto)
                            }
                        } catch (e: Exception) {
                            logger.debug("Пользовательский интерфейс не доступен ${e.message}")
                        }
                    }
                )
            }
            userDialog.open()
        }

        val deleteButton = Button(DELETE_LABEL)

        deleteButton.addClickListener {
            userGrid.selectedItems.forEach { userDto: UserDto ->
                val onSuccess: (t: Void?) -> Unit = { t: Void? ->
                    try {
                        currentUI.access {
                            listDataView?.removeItem(userDto)
                        }
                    } catch (e: Exception) {
                        logger.debug("Пользовательский интерфейс не доступен ${e.message}")
                    }
                }
                userClientService.delete(userDto.id, onSuccess)
            }
        }

        val buttonsLayout = HorizontalLayout()
        buttonsLayout.setWidthFull()

        buttonsLayout.add(addButton, editButton, deleteButton)

        add(buttonsLayout)
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}