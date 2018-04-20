package org.modelcatalogue.core.geb

import geb.Page

class CreateDataElementPage extends Page implements InputUtils {
    static at = { $('.modal-dialog').text().contains('Create Data Element') }

    static content = {
        nameInput { $('#name', 0) }
        descriptionInput { $('#description', 0) }
        modelCatalogueIdInput { $('#modelCatalogueId', 0) }
        submitButton(wait: true) { $("#role_modal_modal-save-elementBtn", 0) }
        searchLink(wait: true) { $("", 0) }
        searchDataType(wait: true) { $("input#dataType") }
        selectItem(wait: true) { $("a.cep-item", 0) }
        createNewDataType(wait: true) { $("a.cep-item.ng-scope.create-new-cep-item") }
        showAllDataTypeLink(wait: true) { $('a', text: "Show All") }
        searchMoreLink(wait: true) { $('span.input-group-addon.search-for-more-icon') }
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void search(String value) {
        fillInput(searchDataType, value)
    }

    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }

    void selectFirstItem(String item) {
        selectItem().click()
    }

    void setModelCatalogueId(String value) {
        fillInput(modelCatalogueIdInput, value)
    }

    void finish() {
        submitButton.click()
    }

    void createDataType() {
        createNewDataType.click()
    }

    void showAllDataType() {
        showAllDataTypeLink.click()
    }

    void searchMore() {
        searchMoreLink.click()
    }
}
