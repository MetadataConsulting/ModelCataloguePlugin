package org.modelcatalogue.core.geb

import geb.Page

class CreatedDataModelNewVersionPage extends Page implements InputUtils {
    static atCheckWaiting = true

    static at = { $("div.modal-header>h4")*.text().join(",").contains('Create new version of') }

    static content = {
        hideButton { $('button', text: 'Hide') }
    }

    void hide() {
        hideButton.click()
    }

}
