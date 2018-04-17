package org.modelcatalogue.core.geb

import geb.Page
import geb.navigator.Navigator

class CreateDataClassPage extends Page implements InputUtils, MetadataUtils {
    static at = { $('.modal-dialog').text().contains('Data Class Wizard') }

    static content = {
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
        buttonPlus { $('span.input-group-btn button.btn-success', 0) }
        rawLink(wait: true) { $('a', text: 'Raw') }
        addMetadataButton(wait: true) { $('div.modal button.btn-success', text: 'Add Metadata') }
        gridCheckbox { $("input[type='checkbox']") }
        headerInput { $('input#group-header') }
        initialNumberOfRowsInput { $('input#repeat-num') }
        maxNoOfRowsInput { $('input#repeat-max') }
        createAnotherButton(wait: true) { $('button.btn.btn-success', 1) }
        exitButton(wait: true) { $('button#exit-wizard') }

        excludeDataElementCheckbox(required: true) { $('input', type: "checkbox", 2) }
        mergeToSingleSectionCheckbox(required: true) { $('input', type: "checkbox", 1) }
        excludeCheckbox(required: true) { $('input.ng-pristine.ng-untouched.ng-valid', 0) }
        formItemLink(required: true) { $('a', text: 'Form (Item)') }
    }

    void addMetadata() {
        addMetadataButton.click()
    }

    void raw() {
        rawLink.click()
    }

    void clickPlus() {
        buttonPlus.click()
    }

    void setDataElement(String value) {
        fillInput(dataElementInput, value)
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

    void setSection(String value) {
        fillInput(sectionTextArea, value)
    }

    void setSectionInstructions(String value) {
        fillInput(sectionInstructionsTextArea, value)
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
        sleep(5_000)
    }

    void exit() {
        exitButton.click()
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void setModelCatalogueId(String value) {
        fillInput(modelCatalogueIdInput, value)
    }

    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }

    void selectGrid() {
        gridCheckbox.click()
    }

    void setMaxNumberOfRows(String value) {
        maxNoOfRowsInput.value(value)
    }

    void createAnother() {
        createAnotherButton.click()
    }

    void setSectionSubtitle(String value) {
        fillInput(sectionSubtitleArea, value)
    }

    void checkExclude() {
        excludeCheckbox.click()
    }

    void checkExcludeDataElement() {
        excludeDataElementCheckbox.click()
    }

    void checkMergeToSingleSection() {
        mergeToSingleSectionCheckbox.click()
    }

    void formItem() {
        formItemLink.click()
    }

    void exitWizard() {
        exitButton.click()
    }

    boolean isEnabled(Navigator parent) {
        if (parent.@disabled == "disabled") {
            return false
        }
        return true
    }

}
