package org.modelcatalogue.core.geb

import geb.Page
import geb.navigator.Navigator

class CreateDataClassPage extends Page implements InputUtils, MetadataUtils {
    static at = { $('.modal-dialog', 0).text().contains('Data Class Wizard') }

    static content = {

        topNavigator { $('ul.tutorial-steps').module(CreateDataClassTopNavigatorModule) }

        nameInput { $('#name', 0) }
        descriptionInput { $('#description', 0) }
        modelCatalogueIdInput { $('#modelCatalogueId', 0) }
        finishButton { $('#step-finish', 0) }
        metadataButton { $('#step-metadata', 0) }
        parentsButton { $('#step-parents', 0) }
        childrenButton { $('#step-children', 0) }
        elementsButton { $('#step-elements', 0) }
        exitButton(required: false, wait: true) { $('#exit-wizard') }

        formSectionLink { $('ul.nav-pills>li:nth-child(1)>a', 0) }
        formGridLink { $('ul.nav-pills>li:nth-child(2)>a', 0) }
        ocurrenceLink { $('ul.nav-pills>li:nth-child(3)>a', 0) }

        appearanceLink(wait: true) { $('ul.nav-pills>li:nth-child(4)>a', 0) }

        labelTextArea { $("textarea#section-label") }

        sectionTextArea { $("textarea#section-title") }

        sectionSubtitleArea { $('textarea#section-subtitle') }

        sectionInstructionsTextArea { $("textarea#section-instructions") }

        formPageNumberTextArea { $("input#form-page-number") }

        minOccursInput(required: false, wait: true, cache: false) { $('input#minOccurs') }

        maxOccursInput(required: false, wait: true, cache: false) { $('input#maxOccurs') }

        localNameInput(required: false, wait: true, cache: false) { $('#local-name') }

        dataElementInput { $('#data-element') }
        createNewDataElementLink(required: false, wait: true) { $('a.create-new-cep-item', 0) }
        createNewDataElementPlusButton(required: false, wait: true) { $('span.input-group-btn button.btn-success') }

        buttonPlus { $('span.input-group-btn button.btn-success', 0) }
        rawLink(wait: true) { $('a', text: 'Raw') }
        addMetadataButton(wait: true) { $('div.modal button.btn-success', text: 'Add Metadata') }
        elementsList(wait: true) { $('ul.dropdown-menu.ng-isolate-scope li') }
        elemenAddedText(wait: true) { $('span.with-pointer.ng-binding') }
        alert(wait: true) { $('div.alert',0) }
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }

    void addMetadata() {
        addMetadataButton.click()
    }

    void verifyDataElementAdded(String val) {
        elemenAddedText.text().contains(val)
    }

    void raw() {
        rawLink.click()
    }

    void clickPlus() {
        buttonPlus.click()
    }

    void setDataElement(String value) {
        fillInput(dataElementInput, value)
        waitFor { createNewDataElementLink } // was sleep(2000)  
    }

    void setAppearanceName(String value) {
        fillInput(localNameInput, value)
    }

    void appearance() {
        appearanceLink.click()
    }

    void setMinOccurs(String value) {
        fillInput(minOccursInput, value)
    }

    void setMaxOccurs(String value) {
        fillInput(maxOccursInput, value)
    }

    void ocurrence() {
        ocurrenceLink.click()
    }

    void setLabel(String value) {
        fillInput(labelTextArea, value)
    }

    void setSectionInstructions(String value) {
        fillInput(sectionInstructionsTextArea, value)
    }

    void setSection(String value) {
        fillInput(sectionTextArea, value)
    }

    void setFormPageNumber(String value) {
        fillInput(formPageNumberTextArea, value)
    }

    void formSection() {
        formSectionLink.click()
    }

    void formGrid() {
        formGridLink.click()
    }

    void elements() {
        elementsButton.click()
    }

    void children() {
        childrenButton.click()
    }

    void parents() {
        parentsButton.click()
    }

    void metadata() {
        metadataButton.click()
    }

    void finish() {
        finishButton.click()
        sleep(5000)
    }

    void exit() {
        exitButton.click()
        sleep(2_000)
    }

    void setModelCatalogueId(String value) {
        fillInput(modelCatalogueIdInput, value)
    }

    void selectGrid() {
        gridCheckbox.click()
    }

    void setMaxNumberOfRows(String value) {
        maxNoOfRowsInput.value(value)
    }

    void searchDataElement(String value) {
        setDataElement(value)
        sleep(2000)
        elementsList.$('a', text: contains(value)).click()
    }

    void createNewElement() {
        createNewDataElementLink.click()
    }

    void createNewElementFromPlusButton() {
        createNewDataElementPlusButton.click()
    }

    Boolean isAlertDisplayed() {
        alert?.displayed
    }
}
