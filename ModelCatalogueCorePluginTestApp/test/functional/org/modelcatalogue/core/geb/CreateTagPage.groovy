package org.modelcatalogue.core.geb

import geb.Page

class CreateTagPage extends Page implements InputUtils {
    static at = { $('.modal-dialog').text().contains('Create Tag') }

    static content = {
        nameInput { $('#name', 0) }
        descriptionInput { $('#description', 0) }
        saveButton { $("a#role_modal_modal-save-elementBtn") }
    }

    void save() {
        saveButton.click()
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }
}
