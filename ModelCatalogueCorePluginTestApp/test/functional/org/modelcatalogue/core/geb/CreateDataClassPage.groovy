package org.modelcatalogue.core.geb

import geb.Page

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
        ocurrenceLink { $('ul.nav-pills>li:nth-child(3)>a', 0) }
        appearanceLink { $('ul.nav-pills>li:nth-child(4)>a', 0) }
        labelTextArea { $( "textarea#section-label") }
        sectionTextArea { $( "textarea#section-title") }
        sectionInstructionsTextArea { $( "textarea#section-instructions") }
        formPageNumberTextArea { $( "input#form-page-number") }
        minOccursInput { $('input#minOccurs') }
        maxOccursInput { $('input#maxOccurs') }

        localNameInput { $('#local-name') }

        dataElementInput { $('#data-element') }
        buttonPlus { $('button.btn-success', 0) }
        rawLink { $('a', text: 'Raw') }
        addMetadataButton { $('div.modal button.btn-success', text: 'Add Metadata') }
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
}
