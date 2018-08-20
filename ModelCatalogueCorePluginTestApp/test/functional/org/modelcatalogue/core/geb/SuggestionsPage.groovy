package org.modelcatalogue.core.geb

import geb.Page

class SuggestionsPage extends Page {

    static url = '/batch/create'

    static at = { title == 'Generate Suggestions' }

    static content = {
        nav { $('#topmenu', 0).module(NavModule) }
        submitButton { $('input', value: "Generate") }
        cancelButton { $('a', text: "Cancel") }
        dataModelOne(wait: true, required: false) { $('#dataModel1ID option', text: contains(it)) }
        dataModelTwo(wait: true, required: false) { $('#dataModel2ID option', text: contains(it)) }
    }

    def generateSuggestion() {
        submitButton.click()
    }

    def cancelSuggestion() {
        cancelButton.click()
    }

    String selectDataModelOne(String dataElementNameA) {
        String modelOneValue = dataModelOne(dataElementNameA).text()
        dataModelOne(dataElementNameA).click()
        return modelOneValue
    }

    String selectDataModelTwo(String dataElementNameB) {
        String modelTwoValue = dataModelTwo(dataElementNameB).text()
        dataModelTwo(dataElementNameB).click()
        return modelTwoValue
    }


}
