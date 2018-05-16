package org.modelcatalogue.core.geb

import geb.Page

class CreateDataModelNewVersionPage extends Page implements InputUtils {
    static atCheckWaiting = true

    static at = { $("div.modal-header>h4").text()?.contains('New Version of') }

    static content = {
        semanticVersion { $('input#semanticVersion') }
        createNewVersionButton { $('a#role_modal_modal-create-new-versionBtn') }
    }

    void setNewVersion(String value) {
        fillInput(semanticVersion, value)
    }

    void createNewVersion() {
        createNewVersionButton.click()
    }
}
