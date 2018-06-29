package org.modelcatalogue.core.geb

import geb.Page

class DataModelAssignNewVersionPage extends Page implements InputUtils {

    static at = { $("div.modal-header>h4").text().contains('New Version of Data Model') }

    static url = '/#'

    static content = {
        semanticVersionInput(wait: true) { $('#semanticVersion') }
        createNewVersionButton(wait: true) { $('a#role_modal_modal-create-new-versionBtn') }
        cancelButton { $('a#role_modal_modal-cancelBtn') }
    }

    void setSemanticVersion(String value) {
        fillInput(semanticVersionInput, value)
    }

    void createNewVersion() {
        createNewVersionButton.click()
    }

    void cancel() {
        cancelButton.click()
    }

}
