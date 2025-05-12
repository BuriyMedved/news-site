package org.buriy.medved.frontend.components

import com.vaadin.flow.component.button.Button

abstract class SaveDialog: DialogBase() {
    
    companion object {
        private const val CANCEL_BUTTON_LABEL = "Отмена"
        private const val SAVE_BUTTON_LABEL = "Сохранить"
    }

    init {
        val saveButton = Button(SAVE_BUTTON_LABEL)
        val cancelButton = Button(CANCEL_BUTTON_LABEL)

        saveButton.addClickListener {
            saveAction()
        }

        cancelButton.addClickListener {
            cancelAction()
        }
        footer.add(saveButton)
        footer.add(cancelButton)
    }

    abstract fun saveAction()
    protected fun cancelAction() {
        close()
    }
}