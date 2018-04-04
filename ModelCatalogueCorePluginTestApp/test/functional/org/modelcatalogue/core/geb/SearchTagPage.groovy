package org.modelcatalogue.core.geb

import geb.Page

class SearchTagPage extends Page implements InputUtils {
    static at = { $("input#value").displayed }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchTagLink(wait: true) { $("h4.list-group-item-heading", text: it) }
    }

    void searchTag(String name) {
        searchTagLink(name).click()
    }
}