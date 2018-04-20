package org.modelcatalogue.core.geb

import geb.Page

class DataTypePage extends Page implements InputUtils {

    static url = '/#'

    static at = { title.startsWith('History of') }

    static content = {
        enumeratedTypeDropdown {
            $('a#role_item_catalogue-element-menu-item-link')
        }
        validateValueLink { $('a#validate-value-menu-item-link') }
        validateKeyField { $('input#value') }
        outputField(required: true) { $('div.alert.alert-success') }
    }

    void enumeratedType() {
        enumeratedTypeDropdown.click()
    }

    void validateValue() {
        validateValueLink.click()
    }

    void setValidateKeyField(String key) {
        fillInput(validateKeyField, key)
    }

    boolean outputIsValid() {
        if (outputField.text().contains("VALID")) {
            return true
        }
        return false
    }

}
