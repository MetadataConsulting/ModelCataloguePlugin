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
        defaultTag { $("a.remove-tag") }
        policiesInput { $("input#dataModelPolicies") }
        policyTag { $("span.with-pointer.ng-binding") }
        dataClasses { $('div.split-view-content').module(DataModelTreeViewModule) }
        policiesCheckboxList { $('label', text: "Policies").siblings('ul').$('li') }
    }

    void submit() {
        submitButton.click()
        sleep(2000)
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
        fillInput(descriptionTextArea, value)
    }

    void removeTag() {
        defaultTag.click()
    }

    void setPolicy(String policyName) {
        fillInput(policiesInput, policyName)
    }

    String getPolicies() {
        policyTag.text()
    }

    List<String> selectedPolicyName() {
        List<String> policiesSelected = new ArrayList<>()
        policiesInput.each { it ->
            if (it.@'checked') {
                policiesSelected.add(it.siblings('span').text())
            }
        }
        return policiesSelected
    }

    void selectUniqueOfKindPolicy() {
        policiesCheckboxList.$('input#dataModelPolicies', 0).click()
    }

    void selectDefaultsChecks() {
        policiesCheckboxList.$('input#dataModelPolicies', 1).click()
    }
}
