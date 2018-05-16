package org.modelcatalogue.core.geb

import geb.Page

class SearchTagPage extends Page implements InputUtils {
    static at = { $("input#value", 0).displayed }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchTagLink(required: false, wait: true) { $("h4.list-group-item-heading", text: it) }
        addImportLink(required: false, wait: true) { $('a', text: 'Add Import') }
        closeLink(wait: true) { $('span.fa-close', 0).parent("div") }
    }

    void searchTag(String name) {
        searchTagLink(name).click()
    }

    void enterSearchBar(String value) {
        fillInput(search, value)
    }

    void addImport() {
        addImportLink.click()
    }

    Boolean noResultFound() {
        sleep(2000)
        $('div.alert-warning div span', text: "No Results").displayed
    }

    void close() {
        waitFor(5) { closeLink }
        closeLink.click()
        sleep(2000)
    }

    Boolean tagPresent(String value) {
        if (searchTagLink(value)) {
            return true

        }
        false
    }
}