package org.buriy.medved.frontend.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.Icon

abstract class DialogBase : Dialog() {
    init{
        val closeIcon = Icon("lumo", "cross")
        val closeButton = Button(closeIcon)
        {
            _ -> close()
        }
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        header.add(closeButton)
    }
}