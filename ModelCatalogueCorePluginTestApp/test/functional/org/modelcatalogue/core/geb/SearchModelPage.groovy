package org.modelcatalogue.core.geb

import geb.Page

class SearchModelPage extends Page implements InputUtils {
    static at = { $("input#value").displayed }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchTagLink(wait: true) { $("h4.list-group-item-heading", text: contains(it)) }
        dataModelList(required: false, wait: true) { $('h4.list-group-item-heading') }
    }

    void searchModelByName(String name) {
        searchTagLink(name).click()
    }

    void selectDataModel(int index) {
        dataModelList[index].click()
    }
}