package org.modelcatalogue.core.geb

import geb.Page

class SearchCatalogElementPage extends Page implements InputUtils {
    static at = { $("input#value", placeholder: "Search for Catalogue Element").displayed }

    static content = {
        search(wait: true) { $("input#value", 0) }
        searchElementLink(wait: true) { $("h4.list-group-item-heading", text: contains(it),0) }
    }

    void searchCatalogElement(String name) {
        searchElementLink(name).click()
    }
}