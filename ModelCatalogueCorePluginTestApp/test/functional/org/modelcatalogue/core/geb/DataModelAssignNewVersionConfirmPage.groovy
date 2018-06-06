package org.modelcatalogue.core.geb

import geb.Page

class DataModelAssignNewVersionConfirmPage extends Page implements InputUtils {

    static at = { $("div.modal-header>h4", 0).text().contains('Create new version of') }

    static url = '/#'

    static content = {
        hideButton { $('button.btn-primary') }
    }

    void hide() {
        hideButton.click()
    }

}
