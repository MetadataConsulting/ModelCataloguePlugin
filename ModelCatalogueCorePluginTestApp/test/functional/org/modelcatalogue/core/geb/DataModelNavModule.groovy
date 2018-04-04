package org.modelcatalogue.core.geb

import geb.Module

class DataModelNavModule extends Module {

    static content = {
        finalizeLink(required: false, wait: true) { $('li#finalize-menu-item a', 0) }
        creteNewVersionLink(required: false, wait: true) { $('li#create-new-version-menu-item a', 0) }
        archiveLink(required: false, wait: true) { $('li#archive-menu-item a', 0) }
        deleteLink(required: false, wait: true) { $('li#delete-menu-item a', 0) }
        addImportLink(required: false,  wait: true) { $('li#add-import-menu-item a', 0) }
        createNewRelationshipLink(required: false, wait: true) { $('li#create-new-relationship-menu-item a', 0) }
        createDataClassLink(required: false, wait: true) { $('li#catalogue-element-create-dataClass-menu-item a', 0) }
        createDataElementLink(required: false, wait: true) { $('li#catalogue-element-create-dataElement-menu-item a', 0) }
        createDataTypeLink(required: false, wait: true) { $('li#catalogue-element-create-dataType-menu-item a', 0) }
        createMeasurementUnitLink(required: false, wait: true) { $('li#catalogue-element-create-measurementUnit-menu-item a', 0) }
        createAssetLink(required: false, wait: true) { $('li#catalogue-element-create-asset-menu-item a', 0) }
        createValidationRuleLink(required: false, wait: true) { $('li#catalogue-element-create-validationRule-menu-item a', 0) }
        mergeLink(required: false, wait: true) { $('li#merge-menu-item a', 0) }
        cloneAnotherIntoCurrentLink(required: false, wait: true) { $('li#clone-from-menu-item a', 0) }
        cloneCurrentIntoAnotherLink(required: false, wait: true) { $('li#clone-menu-item a', 0) }
        reindexDataModelLink(required: false, wait: true) { $('li#reindex-data-model-menu-item a', 0) }
    }

    boolean existsReindexDataModel() {
        !reindexDataModelLink.empty
    }

    boolean reindexDataModel() {
        reindexDataModelLink.click()
    }

    boolean existsFinalize() {
        !finalizeLink.empty
    }

    boolean existsCreateNewVersion() {
        !creteNewVersionLink.empty
    }

    boolean existsArchive() {
        !archiveLink.empty
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
