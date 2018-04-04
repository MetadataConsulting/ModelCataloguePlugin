package org.modelcatalogue.core.geb

import geb.Module

class DataModelNavModule extends Module {

    static content = {
        finalizeLink(required: false, wait: true) { $('li#finalize-menu-item a') }
        creteNewVersionLink(required: false, wait: true) { $('li#create-new-version-menu-item a') }
        archiveLink(required: false, wait: true) { $('li#archive-menu-item a') }
        deleteLink(required: false, wait: true) { $('li#delete-menu-item a') }
        addImportLink(required: false,  wait: true) { $('li#add-import-menu-item a') }
        createNewRelationshipLink(required: false, wait: true) { $('li#create-new-relationship-menu-item a') }
        createDataClassLink(required: false, wait: true) { $('li#catalogue-element-create-dataClass-menu-item a') }
        createDataElementLink(required: false, wait: true) { $('li#catalogue-element-create-dataElement-menu-item a') }
        createDataTypeLink(required: false, wait: true) { $('li#catalogue-element-create-dataType-menu-item a') }
        createMeasurementUnitLink(required: false, wait: true) { $('li#catalogue-element-create-measurementUnit-menu-item a') }
        createAssetLink(required: false, wait: true) { $('li#catalogue-element-create-asset-menu-item a') }
        createValidationRuleLink(required: false, wait: true) { $('li#catalogue-element-create-validationRule-menu-item a') }
        mergeLink(required: false, wait: true) { $('li#merge-menu-item a') }
        cloneAnotherIntoCurrentLink(required: false, wait: true) { $('li#clone-from-menu-item a') }
        cloneCurrentIntoAnotherLink(required: false, wait: true) { $('li#clone-menu-item a') }

    }

    boolean existsDelete() {
        !deleteLink.empty
    }

    boolean existsAddImport() {
        !addImportLink.empty
    }

    boolean existsCreateNewRelationship() {
        !createNewRelationshipLink.empty
    }

    boolean existsCreateDataClass() {
        !createDataClassLink.empty
    }

    boolean existsCreateDataElement() {
        !createDataElementLink.empty
    }

    boolean existsCreateDataType() {
        !createDataTypeLink.empty
    }

    boolean existsCreateMeasurementUnit() {
        !createMeasurementUnitLink.empty
    }

    boolean existsCreateAsset() {
        !createAssetLink.empty
    }

    boolean existsValidationRule() {
        !createValidationRuleLink.empty
    }

    boolean existsMerge() {
        !mergeLink.empty
    }

    boolean existsCloneAnotherIntoCurrent() {
        !cloneAnotherIntoCurrentLink.empty
    }

    boolean existsCloneCurrentIntoAnother() {
        !cloneCurrentIntoAnotherLink.empty
    }

    void finalize() {
        finalizeLink.click()
    }

    void createNewVersion() {
        creteNewVersionLink.click()
    }

    void archive() {
        archiveLink.click()
    }

    void delete() {
        deleteLink.click()
    }

    void addImport() {
        addImportLink.click()
    }

    void createNewRelationship() {
        createNewRelationshipLink.click()
    }

    void createDataClass() {
        createDataClassLink.click()
    }

    void createDataElement() {
        createDataElementLink.click()
    }

    void createDataType() {
        createDataTypeLink.click()
    }

    void createMeasurementUnit() {
        createMeasurementUnitLink.click()
    }

    void createAsset() {
        createAssetLink.click()
    }

    void createValidationRule() {
        createValidationRuleLink.click()
    }

    void merge() {
        mergeLink.click()
    }

    void cloneAnotherIntoCurrent() {
        cloneAnotherIntoCurrentLink.click()
    }

    void cloneCurrentIntoAnother() {
        cloneCurrentIntoAnotherLink.click()
    }
}
