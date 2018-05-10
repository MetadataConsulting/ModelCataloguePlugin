package org.modelcatalogue.core.geb

import geb.Page

class FinalizedDataModelPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4")*.text().join(",").contains('Finalizing') }

    static content = {
        hideButton { $('form.ng-pristine>button.btn.btn-primary') }
    }

    void hideConfirmation() {
        hideButton.click()
        sleep(2000)
    }
}
