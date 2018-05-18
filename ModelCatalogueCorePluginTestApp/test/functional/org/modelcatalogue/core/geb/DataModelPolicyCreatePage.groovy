package org.modelcatalogue.core.geb

import geb.Page

class DataModelPolicyCreatePage extends Page {

    static at = { $('.modal-dialog').text().contains('Create Data Policy') }

    static content = {
        inputName { $('#name', 0) }
        inputPolicyText { $('#policyText', 0) }
        saveButton { $('#role_modal_modal-save-elementBtn', 0) }
    }

    void save() {
        saveButton.click()
        sleep(2000)
    }

    void setName(String value) {
        fillInput(inputName, value)
    }

    void setPolicyText(String value) {
        fillInput(inputPolicyText, value)
    }

    void fillInput(def input, String value) {
        for ( char c : value.toCharArray() ) {
            input << "${c}".toString()
        }
    }
}
