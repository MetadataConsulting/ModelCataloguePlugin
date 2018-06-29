package org.modelcatalogue.core.geb

import geb.Page

class ImportDataModelPage extends Page {

    static at = { $("div.modal-body>form>h4").displayed }

    static content = {
        searchMoreButton { $('span.input-group-addon.search-for-more-icon') }
        createRelationshipButton(wait: true) { $('button.btn-primary', text: "Create Relationship") }
        cancelButton { $('button.btn-warning') }
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void createRelationship() {
        sleep(2_000)
        createRelationshipButton.click()
        sleep(2_000)
    }

    void cancel() {
        cancelButton.click()
    }

}
