package org.modelcatalogue.core.geb

import geb.Page

class DataModelListPage extends Page {

    static url = '/#/dataModels'

    static at = { title == 'Data Models' }

    static content = {
        dashboardButtonLink(wait: true) { $('#dashboard-button-link', 0) }
        createNewButton(wait: true, required: false) { $('#create-new-button', 0) }
    }

    void createNew() {
        createNewButton.click()
    }

    void dashboard() {
        dashboardButtonLink.click()
    }
}
