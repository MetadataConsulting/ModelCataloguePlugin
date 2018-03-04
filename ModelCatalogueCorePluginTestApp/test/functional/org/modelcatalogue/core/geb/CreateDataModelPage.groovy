package org.modelcatalogue.core.geb

import geb.Page

class CreateDataModelPage extends Page {

    static content = {
        wizard(wait: true, required: false) { $('div.create-classification-wizard', 0) }
        stepImports { $('#step-imports', 0) }
        nameInput { $('#name', 0) }
        semanticVersionInput { $('#semanticVersion', 0) }
        modelCatalogueIdInput { $('#modelCatalogueId', 0) }
        descriptionTextArea { $('#description', 0) }
        dataModelButtons { $('#step-classification', 0) }
        importsButton { $('#step-imports', 0) }
        nextButton { $('#step-next', 0) }
        finishButton { $('#step-finish', 0) }
    }

    void nextStep() {
        nextButton.click()
    }

    void finish() {
        finishButton.click()
    }

    void dataModelStep() {
        dataModelButtons.click()
    }

    void importsStep() {
        importsButton.click()
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void setSemanticVersion(String value) {
        fillInput(semanticVersionInput, value)
    }

    void setModelCatalogueIdInput(String value) {
        fillInput(modelCatalogueIdInput, value)
    }

    void setDescription(String value) {
        fillInput(modelCatalogueIdInput, value)
    }

    void fillInput(def input, String value) {
        for ( char c : value.toCharArray() ) {
            input << "${c}".toString()
        }
    }
}
