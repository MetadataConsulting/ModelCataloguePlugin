package org.modelcatalogue.core.geb

import geb.Page

class DataModelListPage extends Page {

    static url = '/#/dataModels'

    static at = { title == 'Data Models' }

    static content = {
        searchInputField { $('input#data-model-search-box') }
        dataModelLink { $('h3.panel-title', title: it).$('a', 0) }
        navbar { $('ul.nav.navbar-nav.navbar-right').module(MainNavigationModule) }
        createButton(required: false) { $('a#role_data-models_create-data-modelBtn', 0) }
    }

    void search(String query) {
        for ( char c : query.toCharArray() ) {
            searchInputField << "${c}"
            sleep(2_000)
        }
    }

    void select(String dataModelName) {
        waitFor {
            dataModelLink(dataModelName).click()
        }
    }

    boolean isCreateButtonDisplayed() {
        createButton.isDisplayed()
    }
}
