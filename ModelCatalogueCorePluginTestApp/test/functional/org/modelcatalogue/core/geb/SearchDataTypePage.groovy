package org.modelcatalogue.core.geb

import geb.Page

class SearchDataTypePage extends Page implements InputUtils {
    static at = { $("input#value").displayed }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchDataTypeLink(wait: true) { $("h4.list-group-item-heading", text: contains(it)) }
    }

    void searchDataType(String name) {
        searchDataTypeLink(name).click()
    }
}