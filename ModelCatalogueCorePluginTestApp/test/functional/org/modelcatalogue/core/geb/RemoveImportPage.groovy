package org.modelcatalogue.core.geb

import geb.Page

class RemoteImportPage extends Page {
    static at = { $("div.modal-header>h4").text().contains('Remove') }

    static content = {
        submitButton(wait: true) { $(" div.modal-footer>form>button.btn.btn-primary") }
    }

    void finish() {
        submitButton.click()
    }
}