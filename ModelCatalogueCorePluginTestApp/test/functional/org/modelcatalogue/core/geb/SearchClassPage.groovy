package org.modelcatalogue.core.geb

import geb.Page

class SearchClassPage extends Page implements InputUtils {
    static at = { $("input#value").displayed }

    static content = {
        searchBar(wait: true) { $("input#value", 0) }
        searchTagLink(wait: true) { $("h4.list-group-item-heading", text: contains(it)) }
        classList(required: false, wait: true) { $('h4.list-group-item-heading') }
    }

    void searchClassByName(String name) {
        searchTagLink(name).click()
    }

    void search(String value) {
        fillInput(searchBar, value)
        Thread.sleep(1000)       //search result take time to appear so need sleep to select searched record
        selectDataClass(0)
    }

    void selectDataClass(int index) {
        classList[index].click()
    }
}