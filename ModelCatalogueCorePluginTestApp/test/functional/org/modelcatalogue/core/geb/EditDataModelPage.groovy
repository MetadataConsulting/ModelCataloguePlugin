package org.modelcatalogue.core.geb

import geb.Page

class EditDataModelPage extends Page implements InputUtils {
    static at = { name?.displayed }

    static content = {

        name(wait: true) { $("#name", 0) }
        description(wait: true) { $("#description", 0) }
        submitButton(wait: true) { $("#role_modal_modal-save-elementBtn") }
    }

    void fillName(String searchText) {
        name.value("")
        fillInput(name, searchText)
    }

    void fillDescription(String searchText) {
        fillInput(description, searchText)
    }

    void submitBttn() {
        submitButton.click()
    }
}

