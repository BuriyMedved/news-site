package org.buriy.medved.frontend.admin

import com.vaadin.flow.component.grid.Grid
import org.buriy.medved.backend.dto.UserDto

class UserGrid(pageSize: Int = 50) : Grid<UserDto>(pageSize){
    companion object{
        private const val LOGIN_FIELD_TITLE = "Логин"
        private const val NAME_FIELD_TITLE = "Псевдоним"
        private const val EMAIL_FIELD_TITLE = "Почта"
        private const val ROLES_FIELD_TITLE = ""
    }
    init {
//        addComponentColumn { EleronWrapTextLabel(it.lastName) }.setHeader(ZHuman.LAST_NAME_FIELD).isAutoWidth = true
        addColumn { it.login }.setHeader(LOGIN_FIELD_TITLE).isAutoWidth = true
        addColumn { it.name }.setHeader(NAME_FIELD_TITLE).isAutoWidth = true
        addColumn { it.email }.setHeader(EMAIL_FIELD_TITLE).isAutoWidth = true
    }
}