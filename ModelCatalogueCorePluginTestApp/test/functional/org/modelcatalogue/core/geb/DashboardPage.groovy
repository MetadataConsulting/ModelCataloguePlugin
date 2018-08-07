package org.modelcatalogue.core.geb

import geb.Page

class DashboardPage extends Page {

    static url = '/dashboard/index'

    static at = { title == 'Dashboard' }

    static content = {
        searchInputField(wait: true) { $('#search', 0) }
        dataModelLinks(required: false) { $('a.data-model-link') }
        searchButton(wait: true) { $('#search-btn') }
        dataModelLink(wait: true) { $('a.data-model-link', text: it, 0) }
        nav { $('#topmenu', 0).module(NavModule) }
        sponsor { $('.sponsor', it) }
    }

    void search(String query) {
        for (char c : query.toCharArray()) {
            searchInputField << "${c}"
        }
        searchButton.click()
        waitFor(5) { dataModelLink(query) }
    }

    void sponsorLink(int val) {
        sponsor(val).click()
    }

    void select(String dataModelName) {
        sleep(1_000)
        dataModelLink(dataModelName).click()
    }

    void selectFirst() {
        dataModelLinks.first().click()
    }

    int count() {
        if (dataModelLinks.empty) {
            return 0
        }
        dataModelLinks.size()
    }

    void selectModelByNameAndIndex(String name, Integer index) {
        wait(2_000)
        dataModelLink(name)[index].click()
        wait(1_000)

    }
}
