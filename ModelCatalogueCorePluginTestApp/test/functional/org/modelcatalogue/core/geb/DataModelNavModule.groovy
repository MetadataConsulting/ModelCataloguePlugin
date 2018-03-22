package org.modelcatalogue.core.geb

import geb.Module

class DataModelNavModule extends Module {

    static content = {
        finalizeLink(wait: true) { $('li#finalize-menu-item a') }
        creteNewVersionLink(wait: true) { $('li#create-new-version-menu-item a') }
        archiveLink(wait: true) { $('li#archive-menu-item a') }
        deleteLink(wait: true) { $('li#delete-menu-item a') }
        addImportLink(wait: true) { $('li#add-import-menu-item a') }
        createNewRelationshipLink(wait: true) { $('li#create-new-relationship-menu-item a') }
        createDataClassLink(wait: true) { $('li#catalogue-element-create-dataClass-menu-item a') }
        createDataElementLink(wait: true) { $('li#catalogue-element-create-dataElement-menu-item a') }
        createDataTypeLink(wait: true) { $('li#catalogue-element-create-dataType-menu-item a') }
        createMeasurementUnitLink(wait: true) { $('li#catalogue-element-create-measurementUnit-menu-item a') }
        createAssetLink(wait: true) { $('li#catalogue-element-create-asset-menu-item a') }
        createValidationRuleLink(wait: true) { $('li#catalogue-element-create-validationRule-menu-item a') }
        mergeLink(wait: true) { $('li#merge-menu-item a') }
        cloneAnotherIntoCurrentLink(wait: true) { $('li#clone-from-menu-item a') }
        cloneCurrentIntoAnotherLink(wait: true) { $('li#clone-menu-item a') }
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
