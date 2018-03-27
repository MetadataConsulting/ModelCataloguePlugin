package org.modelcatalogue.core.geb

import geb.Page
import geb.module.Checkbox

class CreateDataModelPage extends Page implements InputUtils {

    static url = '/dataModel/create'

    static at = { title == 'Create Data Model' }

    static content = {
        wizard(wait: true, required: false) { $('div.create-classification-wizard', 0) }
        stepImports { $('#step-imports', 0) }
        nameInput { $('#name', 0) }
        semanticVersionInput { $('#semanticVersion', 0) }
        modelCatalogueIdInput { $('#modelCatalogueId', 0) }
        descriptionTextArea { $('#description', 0) }
        field { $('li.checkbox span', text: it).parent() }
        fieldCheckbox { field(it).find('input', type: 'checkbox', 0).module(Checkbox) }
        submitButton { $('#createdatamodel-submit', 0) }
    }

    void submit() {
        submitButton.click()
    }

    void check(String name) {
        fieldCheckbox(name).check()
    }

    void uncheck(String name) {
        fieldCheckbox(name).uncheck()
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void setModelCatalogueId(String value) {
        fillInput(modelCatalogueIdInput, value)
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


}
