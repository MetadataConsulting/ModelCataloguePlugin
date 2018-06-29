package org.modelcatalogue.core.geb

import geb.Page

class SuggestionsPage extends Page {

    static url = '/batch/create'

    static at = { title == 'Generate Suggestions' }

    static content = {
        nav { $('#topmenu', 0).module(NavModule) }
        submitButton { $('input', value: "Generate") }
        cancelButton { $('a', text: "Cancel") }
        dataModelOne { $('#dataModel1ID') }
        dataModelTwo { $('#dataModel2ID') }
    }

    def generateSuggestion() {
        submitButton.click()
    }

    def cancelSuggestion() {
        cancelButton.click()
    }

    String selectDataModelOne() {
        String modelOneValue = dataModelOne.$("option", selected: "selected").text()
        return modelOneValue
    }

    String selectDataModelTwo() {
        String modelTwoValue = dataModelTwo.$("option", selected: "selected").text()
        return modelTwoValue
    }


}
