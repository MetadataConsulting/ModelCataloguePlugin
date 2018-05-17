package org.modelcatalogue.core.geb

import geb.Page

class ApiKeyPage extends Page {

    static url = '/apiKey/index'

    static at = { title == 'Api Key' }

    static content = {
        inputSubmit { $('input#regenerateKey', 0) }
        apiKeyElement { $('#apiKey', 0) }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    String getApiKey() {
        apiKeyElement.text()
    }

    void regenerate() {
        inputSubmit.click()
    }
}
