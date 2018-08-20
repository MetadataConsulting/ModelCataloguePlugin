package org.modelcatalogue.core.geb

import geb.Page

class DropDownImportPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4").text().contains('Import') }

    static content = {
        search(wait: true) { $("input#elements", 0) }
        searchMoreLink(wait: true) { $("a.show-more-cep-item", 0) }
        submitButton(wait: true) { $("div.modal-footer>button.btn-primary") }
        searchMoreIcon(wait: true) { $("span.input-group-addon") }
    }

    void fillSearchBox(String searchText) {
        fillInput(search, searchText)
        waitFor { searchMoreLink }
    }

    void searchMore() {
        searchMoreLink.click()
    }

    void searchMoreIconButton() {
        searchMoreIcon.click()
    }

    void finish() {
        submitButton.click()
    }
}