package org.modelcatalogue.core.geb

import geb.Page

class CloneDataModelPage extends Page implements InputUtils {
    static at = { $("div.modal-header>h4", 0).text().contains('Clone') }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchMoreLink(wait: true) { $("span.search-for-more-icon", 0) }
        submitButton(wait: true) { $("div.modal-footer button.btn-primary", 0) }
        cancelButton(wait: true) { $('div.modal-footer button.btn-warning', 0) }
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

    void close() {
        waitFor(10) { cancelButton }
        cancelButton.click()
    }
}