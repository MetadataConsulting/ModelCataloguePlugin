package org.modelcatalogue.core.geb

import geb.Page

class ParentClassModalPage extends Page {

    static at = { title.contains("Parents of") }

    static content = {
        searchMoreButton(required: false, wait: true) { $('span.input-group-addon.search-for-more-icon') }
        dataClassList(required: false, wait: true) { $('h4.list-group-item-heading') }
        createRelationshipButton(required: false, wait: true) { $('button.btn.btn-primary', type: 'submit') }
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void importDataClass() {
        dataClassList[0].click()
    }

    void createRelationship() {
        createRelationshipButton.click()
    }
}
