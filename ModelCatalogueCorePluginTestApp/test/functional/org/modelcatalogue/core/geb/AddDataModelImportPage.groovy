package org.modelcatalogue.core.geb

import geb.Page

class AddDataModelImportPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4")*.text().join(",").contains('Add Data Model Import') }

    static content = {
        searchMoreButton { $('span.search-for-more-icon', 0) }
        searchBar { $('input#elements') }
        okButton { $('button', text: "OK") }
        cancelButton { $('button', text: "Cancel") }
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void importDataModel() {
        okButton.click()
    }
}
