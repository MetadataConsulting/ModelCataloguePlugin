package org.modelcatalogue.core.geb

import geb.Page

class SearchDataModelPage extends Page implements InputUtils {
    static at = { $("input#value", 0).displayed }

    static content = {
        searchBar(wait: true) { $("input#value", 0) }
        searchDataModelLink(wait: true) { $("h4.list-group-item-heading", text: contains(it)) }
        addImportLink { $('a', text: 'Add Import', 0) }
    }

    void searchDataModel(String name) {
        searchDataModelLink(name).click()
    }

    void addImport() {
        addImportLink.click()
    }
}