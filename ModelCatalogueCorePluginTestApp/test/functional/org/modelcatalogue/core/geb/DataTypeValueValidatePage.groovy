package org.modelcatalogue.core.geb

import geb.Page

class DataTypeValueValidatePage extends Page implements InputUtils {

    static url = '/#'

    static at = { $("div.modal-header>h4").text()?.contains('Validate Value') }

    static content = {
        validateKeyField { $('input#value') }
        outputField(wait: true) { $('div.alert.alert-success') }
    }

    void setValidateKeyField(String key) {
        fillInput(validateKeyField, key)
    }

    void clearKeyField() {
        validateKeyField.value("")
    }

    boolean outputIsValid() {
        sleep(2_000)
        outputField.text().contains("VALID")
    }

}
