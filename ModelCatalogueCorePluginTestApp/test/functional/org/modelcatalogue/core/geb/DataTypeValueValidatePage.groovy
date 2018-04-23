package org.modelcatalogue.core.geb

import geb.Page

class DataTypeValueValidatePage extends Page implements InputUtils {

    static url = '/#'

    static at = { $("div.modal-header>h4").text()?.contains('Validate Value') }

    static content = {
        validateKeyField { $('input#value') }
        outputField(required: true) { $('div.alert.alert-success') }
    }

    void setValidateKeyField(String key) {
        validateKeyField.value(key)
    }

    boolean outputIsValid() {
        if (outputField.text().contains("VALID")) {
            return true
        }
        return false
    }

}
