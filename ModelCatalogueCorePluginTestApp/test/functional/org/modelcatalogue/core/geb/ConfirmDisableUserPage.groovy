package org.modelcatalogue.core.geb

import geb.Page

class ConfirmDisableUserPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4")*.text().join(",").contains('Disable User') }

    static content = {
        confirmButton { $('button.btn-primary') }
        cancelButton { $('button.btn-warning') }
    }

    void confirmDisableUser() {
        confirmButton.click()
    }

    void cancel() {
        cancelButton.click()
    }
}
