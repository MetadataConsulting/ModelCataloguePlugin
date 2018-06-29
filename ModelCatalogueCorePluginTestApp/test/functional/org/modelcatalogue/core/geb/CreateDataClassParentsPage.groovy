package org.modelcatalogue.core.geb

import geb.Page
import geb.navigator.Navigator

class CreateDataClassParentsPage extends Page implements InputUtils, MetadataUtils {
    static at = { $('div#parents div.form-group label', 0).text().contains('Parent Data Class') }

    static content = {

        topNavigator { $('ul.tutorial-steps').module(CreateDataClassTopNavigatorModule) }

        formSectionLink { $('ul.nav-pills>li:nth-child(1)>a', 0) }
        formGridLink { $('ul.nav-pills>li:nth-child(2)>a', 0) }
        ocurrenceLink { $('ul.nav-pills>li:nth-child(3)>a', 0) }
        appearanceLink(wait: true) { $('ul.nav-pills>li:nth-child(4)>a', 0) }
        rawLink(wait: true) { $('ul.nav-pills>li:nth-child(5)>a', 0) }

        excludeDataElementCheckbox(required: true) { $('input', type: "checkbox", 2) }
        mergeToSingleSectionCheckbox(required: true) { $('input', type: "checkbox", 1) }
        excludeCheckbox(required: true) { $('input', type: "checkbox", 0) }
        labelTextArea { $("textarea#section-label") }
        sectionTextArea { $("textarea#section-title") }
        sectionSubtitleArea { $('textarea#section-subtitle') }
        sectionInstructionsTextArea { $("textarea#section-instructions") }
        formPageNumberTextArea { $("input#form-page-number") }

        gridCheckbox { $("input[type='checkbox']") }
        headerInput { $('input#group-header') }
        initialNumberOfRowsInput { $('input#repeat-num') }
        maxNoOfRowsInput { $('input#repeat-max') }

        minOccursInput(required: false, wait: true, cache: false) { $('input#minOccurs') }
        maxOccursInput(required: false, wait: true, cache: false) { $('input#maxOccurs') }

        localNameInput(required: false, wait: true, cache: false) { $('#local-name') }

        addMetadataButton(wait: true) { $('div.modal button.btn-success', text: 'Add Metadata') }
    }

    void formSection() {
        formSectionLink.click()
    }

    void formGrid() {
        formGridLink.click()
    }

    void ocurrence() {
        ocurrenceLink.click()
    }

    void appearance() {
        appearanceLink.click()
    }

    void raw() {
        rawLink.click()
    }

    void checkExcludeDataElement() {
        excludeDataElementCheckbox.click()
    }

    void checkExclude() {
        excludeCheckbox.click()
    }

    void checkMergeToSingleSection() {
        mergeToSingleSectionCheckbox.click()
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

    void setSectionSubtitle(String value) {
        fillInput(sectionSubtitleArea, value)
    }

    void selectGrid() {
        gridCheckbox.click()
    }

    void setHeader(String value) {
        fillInput(headerInput, value)
    }

    boolean headerInputIsEnabled() {
        isEnabled(headerInput)
    }

    void setinitialNumberOfRows(String value) {
        fillInput(initialNumberOfRowsInput, value)
    }

    boolean initialNumberOfRowsIsEnabled() {
        isEnabled(initialNumberOfRowsInput)
    }

    void setMaxNumberOfRows(String value) {
        fillInput(maxNoOfRowsInput, value)
    }

    boolean maxNumberOfRowsIsEnabled() {
        isEnabled(maxNoOfRowsInput)
    }

    void setMinOccurs(String value) {
        fillInput(minOccursInput, value)
    }

    void setMaxOccurs(String value) {
        fillInput(maxOccursInput, value)
    }

    void setAppearanceName(String value) {
        fillInput(localNameInput, value)
    }

    void addMetadata() {
        addMetadataButton.click()
    }

    boolean isEnabled(Navigator parent) {
        !(parent.@disabled == "disabled")
    }

}
