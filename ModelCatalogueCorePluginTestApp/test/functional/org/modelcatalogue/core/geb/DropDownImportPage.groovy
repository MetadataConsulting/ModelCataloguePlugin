package org.modelcatalogue.core.geb

import geb.Page

class DropDownImportPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4").text().contains('Import') }

    static content = {
        search(wait: true) { $("input#elements", 0) }
        searchMoreLink(wait: true) { $("a.show-more-cep-item", 0) }
        submitButton(wait: true) { $("div.modal-footer>button.btn-primary") }
    }

    void fillSearchBox(String searchText) {
        fillInput(search, searchText)
    }

    void searchMore() {
        searchMoreLink.click()
    }

    void finish() {
        submitButton.click()
    }
}