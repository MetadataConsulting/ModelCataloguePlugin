package org.modelcatalogue.core.geb

import geb.Page
import geb.navigator.Navigator

class CreateDataClassElementsPage extends Page implements InputUtils, MetadataUtils {
    static at = { $('div#elements div.form-group label', 0).text().contains('Data Element') }

    static content = {

        topNavigator { $('ul.tutorial-steps').module(CreateDataClassTopNavigatorModule) }

        formItemLink(required: true) { $('a', text: 'Form (Item)') }
        ocurrenceLink { $('ul.nav-pills>li:nth-child(2)>a', 0) }
        appearanceLink(wait: true) { $('ul.nav-pills>li:nth-child(3)>a', 0) }
        rawLink(wait: true) { $('ul.nav-pills>li:nth-child(4)>a', 0) }

        excludeCheckbox { $('input', type: "checkbox", 0) }
        nameInput { $('input#item-name') }
        protectHealthInfoCheckbox { $('input', type: "checkbox", 1) }
        questionInput { $('input#item-question') }
        descriptionInput { $('textarea#item-description') }
        defaultValueInput { $('input#item-default-value') }
        instructionsInput { $('textarea#item-instructions') }
        requiredCheckbox { $('input', type: "checkbox", 2) }
        horizontalCheckbox { $('input', type: "checkbox", 3) }
        columnNumberInput { $('input#item-column-number') }
        questionNumberInput { $('input#item-question-number') }
        dataTypeDropdown { $('select#item-data-type') }
        dataTypeValue { $('#item-data-type option', text: it) }
        responseTypeDropdown { $('select#item-response-type') }
        responseTypeValue { $('#item-response-type option', text: it) }
        unitsInput { $('input#item-units') }
        maxLengthInput { $('input#item-length') }
        numberOfDecimalDigitInput { $('input#item-digits') }
        regularExpressionInput { $('input#item-regexp') }
        regularExpressionErrorInput { $('input#item-regexp-message') }

        minOccursInput(required: false, wait: true, cache: false) { $('input#minOccurs') }
        maxOccursInput(required: false, wait: true, cache: false) { $('input#maxOccurs') }

        localNameInput(required: false, wait: true, cache: false) { $('#local-name') }

        addMetadataButton(wait: true) { $('div.modal button.btn-success', text: 'Add Metadata') }

    }

    void formItem() {
        formItemLink.click()
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

    void checkExcludeCheckbox() {
        excludeCheckbox.click()
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void checkProtectHealthInfoCheckbox() {
        protectHealthInfoCheckbox.click()
    }

    void setQuestion(String value) {
        fillInput(questionInput, value)
    }

    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }

    void setDefaultValue(String value) {
        fillInput(defaultValueInput, value)
    }

    void setInstruction(String value) {
        fillInput(instructionsInput, value)
    }

    void selectRequiredCheckbox() {
        requiredCheckbox.click()
    }

    void selectHorizontalCheckbox() {
        horizontalCheckbox.click()
    }

    void setColumnNumber(String value) {
        fillInput(columnNumberInput, value)
    }

    void setQuestionNumber(String value) {
        fillInput(questionNumberInput, value)
    }

    void openDataTypeDropdown() {
        dataTypeDropdown.click()
    }

    void selectDataTypeByName(String value) {
        dataTypeValue(value).click()
    }

    void openResponseTypeDropdown() {
        responseTypeDropdown.click()
    }

    void selectResponseTypeByName(String value) {
        responseTypeValue(value).click()
    }

    void setUnits(String value) {
        fillInput(unitsInput, value)
    }

    void setMaxLenght(String value) {
        fillInput(maxLengthInput, value)
    }

    void setNumberOfDecimalDigit(String value) {
        fillInput(numberOfDecimalDigitInput, value)
    }

    void setRegularExpression(String value) {
        fillInput(regularExpressionInput, value)
    }

    void setRegularExpresssionError(String value) {
        fillInput(regularExpressionErrorInput, value)
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

}
