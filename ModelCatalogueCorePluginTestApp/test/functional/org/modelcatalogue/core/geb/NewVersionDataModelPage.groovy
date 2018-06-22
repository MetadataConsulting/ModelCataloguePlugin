package org.modelcatalogue.core.geb

import geb.Page

class NewVersionDataModelPage extends Page implements InputUtils {
    static atCheckWaiting = true

    static at = { $("div.modal-header>h4").text()?.contains('New Version of Data Model') }

    static content = {
        semanticVersion(wait: true) { $('input#semanticVersion') }
        createVersionButton(wait: true) { $('div.contextual-actions.ng-isolate-scope.btn-toolbar', 0) }
        hideButton(wait: true) { $('form.ng-pristine>button.btn.btn-primary') }
    }

    void hideConfirmation() {
        hideButton.click()
        sleep(2_000)
    }

    void semanticVersionText(String value) {
        fillInput(semanticVersion, value)
    }

    void submit() {
        createVersionButton.click()
        sleep(3_000)
    }
}
