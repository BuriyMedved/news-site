package org.buriy.medved.frontend.admin

import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import org.buriy.medved.backend.consts.RolesNames
import org.buriy.medved.backend.dto.UserDto
import org.buriy.medved.frontend.components.SaveDialog


class UserDialog(
    private val userDto: UserDto,
    private val isNew: Boolean,
    private val onSaveAction: () -> Unit,
): SaveDialog() {
    companion object{
        private const val ADD_TITLE = "Добавление пользователя"
        private const val EDIT_TITLE = "Редактирование пользователя"
        private const val ROLES_LABEL = "Роли"
        private const val LOGIN_FIELD_LABEL = "Логин"
        private const val PASSWORD_FIELD_LABEL = "Пароль"
        private const val DISPLAY_NAME_FIELD_LABEL = "Псевдоним"
        private const val EMAIL_FIELD_LABEL = "Почта"
        private const val EMPTY_LOGIN_WARNING = "Обязательно укажите логин"
        private const val EMPTY_PASSWORD_WARNING = "Обязательно укажите пароль"
        private const val EMPTY_EMAIL_WARNING = "Обязательно укажите почту"
        private const val EMPTY_DISPLAY_NAME_WARNING = "Обязательно укажите псевдоним"
        private const val EDIT_PASSWORD_LABEL = "Изменить пароль"
    }

    private val binder: Binder<UserDto> = Binder(UserDto::class.java)

    init{
        headerTitle = getDialogTitle()

        val dataLayout = VerticalLayout()
        dataLayout.setSizeFull()

        val loginField = TextField()
        loginField.label = LOGIN_FIELD_LABEL

        val passwordField = PasswordField()
        passwordField.label = PASSWORD_FIELD_LABEL

        val displayNameField = TextField()
        displayNameField.label = DISPLAY_NAME_FIELD_LABEL

        val emailField = TextField()
        emailField.label = EMAIL_FIELD_LABEL

        val rolesGroup = CheckboxGroup<String>()
        rolesGroup.label = ROLES_LABEL
        rolesGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)

        val rolesList = ArrayList<String>()
        val listDataView = rolesGroup.setItems(rolesList)

        RolesNames.entries.forEach{ rolesNames: RolesNames ->
            listDataView.addItem(rolesNames.value)
        }

        rolesGroup.select(userDto.roles)

        binder.forField(loginField)
            .asRequired(EMPTY_LOGIN_WARNING)
            .bind(UserDto::login, UserDto::login.setter)

        binder.forField(passwordField)
//            .asRequired(EMPTY_PASSWORD_WARNING)
            .bind(UserDto::password, UserDto::password.setter)

        binder.forField(displayNameField)
            .asRequired(EMPTY_DISPLAY_NAME_WARNING)
            .bind(UserDto::name, UserDto::name.setter)

        binder.forField(emailField)
            .asRequired(EMPTY_EMAIL_WARNING)
            .bind(UserDto::email, UserDto::email.setter)

        binder.forField(rolesGroup)
              .bind(UserDto::roles, UserDto::roles.setter)

        binder.readBean(userDto)

        dataLayout.add(loginField)

        if(!isNew){
            val editPasswordSwitch = Checkbox(EDIT_PASSWORD_LABEL)
            passwordField.isEnabled = false
            editPasswordSwitch.addClickListener {
                passwordField.isEnabled = editPasswordSwitch.value
            }

            dataLayout.add(editPasswordSwitch)
        }

        dataLayout.add(passwordField, displayNameField, emailField, rolesGroup)

        add(dataLayout)
    }

    override fun saveAction() {
        binder.writeBean(userDto)
        onSaveAction()
        close()
    }

    private fun getDialogTitle(): String {
        return if(isNew){
            ADD_TITLE
        }
        else{
            EDIT_TITLE
        }
    }
}