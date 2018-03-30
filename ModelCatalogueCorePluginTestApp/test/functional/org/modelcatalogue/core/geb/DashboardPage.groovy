package org.modelcatalogue.core.geb

import geb.Page

class DashboardPage extends Page {

    static url = '/dashboard/index'

    static at = { title == 'Dashboard' }

    static content = {
        searchInputField { $('#search', 0) }
        filterButton { $('#filter-btn') }
        dataModelLinks(required: false) { $('a.data-model-link') }
        dataModelLink(wait: true) { $('a.data-model-link', text: it) }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    void search(String query) {
        for (char c : query.toCharArray()) {
            searchInputField << "${c}"
        }
        filterButton.click()
    }

    void select(String dataModelName) {
        dataModelLink(dataModelName).click()
    }

    void selectFirst() {
        dataModelLinks.first().click()
    }

    int count() {
        if ( dataModelLinks.empty ) {
            return 0
        }
        dataModelLinks.size()
    }
}
