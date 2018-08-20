package org.modelcatalogue.core.geb

import geb.Page

class ConfirmEnableUserPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4")*.text().join(",").contains('Enable User') }

    static content = {
        confirmButton { $('button.btn-primary') }
        cancelButton { $('button.btn-warning') }
    }

    void confirmEnableUser() {
        confirmButton.click()
    }

    void cancel() {
        cancelButton.click()
    }
}
