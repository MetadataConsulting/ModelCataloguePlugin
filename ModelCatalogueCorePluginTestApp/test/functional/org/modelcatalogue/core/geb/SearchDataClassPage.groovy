package org.modelcatalogue.core.geb

import geb.Page

class SearchDataClassPage extends Page implements InputUtils {
    static at = { $("input#value", 0).displayed }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchDataClassLink(wait: true) { $("h4.list-group-item-heading", text: contains(it)) }
        addImportLink { $('a', text: 'Add Import') }
    }

    void selectDataClass(String name) {
        searchDataClassLink(name).click()
    }

    void addImport() {
        addImportLink.click()
    }

    void searchDataClass(String value) {
        fillInput(search, value)
        sleep(2000)
        selectDataClass(value)
    }
}