package org.modelcatalogue.core.geb

import geb.Page

class CreateDataElementPage extends Page implements InputUtils {
    static at = { $('.modal-dialog', 0).text().contains('Create Data Element') }

    static content = {
        nameInput { $('#name', 0) }
        descriptionInput { $('#description', 0) }
        modelCatalogueIdInput { $('#modelCatalogueId', 0) }
        submitButton(wait: true) { $("#role_modal_modal-save-elementBtn", 0) }
        searchDataType(wait: true) { $("input#dataType") }
        selectItem(wait: true) { $("a.cep-item", 0) }
        createNewItem(wait: true) { $("a.cep-item", 1) }
        createNewDataTypeLink(required: false, wait: true) { $('a.create-new-cep-item', 0) }
        searchMoreButton(wait: true) { $('span.search-for-more-icon') }
        createNewDataType(wait: true) { $("a.cep-item.ng-scope.create-new-cep-item") }
        showAllDataTypeLink(wait: true) { $('a', text: "Show All") }
        searchMoreLink(wait: true) { $('span.input-group-addon.search-for-more-icon') }
        createNewDataType(wait: true) { $("a.cep-item.ng-scope.create-new-cep-item") }
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void search(String value) {
        fillInput(searchDataType, value)
        waitFor { createNewDataTypeLink }
    }

    Boolean matchSearch(String value) {
        return true
    }

    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }

    void setModelCatalogueId(String value) {
        fillInput(modelCatalogueIdInput, value)
    }

    void selectFirstItem() {
        selectItem().click()
    }

    void createNewItemClick() {
        createNewItem().click()
    }

    void finish() {
        submitButton.click()
        sleep(2000)
    }

    void createNewDataType() {
        createNewDataTypeLink.click()
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void createDataType() {
        createNewDataType.click()
    }

    void showAllDataType() {
        showAllDataTypeLink.click()
    }

    void createDataType() {
        createNewDataType.click()
    }
}
