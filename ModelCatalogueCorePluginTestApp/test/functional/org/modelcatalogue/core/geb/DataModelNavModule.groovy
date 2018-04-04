package org.modelcatalogue.core.geb

import geb.Browser
import geb.Module

class DataModelNavModule extends Module {

    public static final String finalizeLinkSelector = 'li#finalize-menu-item a'
    public static final String creteNewVersionLinkSelector = 'li#create-new-version-menu-item a'
    public static final String archiveLinkSelector = 'li#archive-menu-item a'
    public static final String deleteLinkSelector = 'li#delete-menu-item a'
    public static final String addImportLinkSelector = 'li#add-import-menu-item a'
    public static final String createNewRelationshipLinkSelector = 'li#create-new-relationship-menu-item a'
    public static final String createDataClassLinkSelector = 'li#catalogue-element-create-dataClass-menu-item a'
    public static final String createDataElementLinkSelector = 'li#catalogue-element-create-dataElement-menu-item a'
    public static final String createDataTypeLinkSelector = 'li#catalogue-element-create-dataType-menu-item a'
    public static final String createMeasurementUnitLinkSelector = 'li#catalogue-element-create-measurementUnit-menu-item a'
    public static final String createAssetLinkSelector = 'li#catalogue-element-create-asset-menu-item a'
    public static final String createValidationRuleLinkSelector = 'li#catalogue-element-create-validationRule-menu-item a'
    public static final String mergeLinkSelector = 'li#merge-menu-item a'
    public static final String cloneAnotherIntoCurrentLinkSelector = 'li#clone-from-menu-item a'
    public static final String cloneCurrentIntoAnotherLinkSelector = 'li#clone-menu-item a'
    public static final String reindexDataModelLinkSelector = 'li#reindex-data-model-menu-item a'

    static content = {
<<<<<<< HEAD
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

=======
        finalizeLink(required: false, wait: true) { $(finalizeLinkSelector, 0) }
        creteNewVersionLink(required: false, wait: true) { $(creteNewVersionLinkSelector, 0) }
        archiveLink(required: false, wait: true) { $(archiveLinkSelector, 0) }
        deleteLink(required: false, wait: true) { $(deleteLinkSelector, 0) }
        addImportLink(required: false,  wait: true) { $(addImportLinkSelector, 0) }
        createNewRelationshipLink(required: false, wait: true) { $(createNewRelationshipLinkSelector, 0) }
        createDataClassLink(required: false, wait: true) { $(createDataClassLinkSelector, 0) }
        createDataElementLink(required: false, wait: true) { $(createDataElementLinkSelector, 0) }
        createDataTypeLink(required: false, wait: true) { $(createDataTypeLinkSelector, 0) }
        createMeasurementUnitLink(required: false, wait: true) { $(createMeasurementUnitLinkSelector, 0) }
        createAssetLink(required: false, wait: true) { $(createAssetLinkSelector, 0) }
        createValidationRuleLink(required: false, wait: true) { $(createValidationRuleLinkSelector, 0) }
        mergeLink(required: false, wait: true) { $(mergeLinkSelector, 0) }
        cloneAnotherIntoCurrentLink(required: false, wait: true) { $(cloneAnotherIntoCurrentLinkSelector, 0) }
        cloneCurrentIntoAnotherLink(required: false, wait: true) { $(cloneCurrentIntoAnotherLinkSelector, 0) }
        reindexDataModelLink(required: false, wait: true) { $(reindexDataModelLinkSelector, 0) }
    }

    boolean existsReindexDataModel(Browser browser) {
        !browser.find(reindexDataModelLinkSelector).empty
    }

    boolean reindexDataModel() {
        reindexDataModelLink.click()
    }

    boolean existsFinalize(Browser browser) {
        !browser.find(finalizeLinkSelector).empty
    }

    boolean existsCreateNewVersion(Browser browser) {
        !browser.find(creteNewVersionLinkSelector).empty
    }

    boolean existsArchive(Browser browser) {
        !browser.find(archiveLinkSelector).empty
>>>>>>> use selector with find
    }

    boolean existsDelete(Browser browser) {
        !browser.find(deleteLinkSelector).empty
    }

    boolean existsAddImport(Browser browser) {
        !browser.find(addImportLinkSelector).empty
    }

    boolean existsCreateNewRelationship(Browser browser) {
        !browser.find(createNewRelationshipLinkSelector).empty
    }

    boolean existsCreateDataClass(Browser browser) {
        !browser.find(createDataClassLinkSelector).empty
    }

    boolean existsCreateDataElement(Browser browser) {
        !browser.find(createDataElementLinkSelector).empty
    }

    boolean existsCreateDataType(Browser browser) {
        !browser.find(createDataTypeLinkSelector).empty
    }

    boolean existsCreateMeasurementUnit(Browser browser) {
        !browser.find(createMeasurementUnitLinkSelector).empty
    }

    boolean existsCreateAsset(Browser browser) {
        !browser.find(createAssetLinkSelector).empty
    }

    boolean existsValidationRule(Browser browser) {
        !browser.find(createValidationRuleLinkSelector).empty
    }

    boolean existsMerge(Browser browser) {
        !browser.find(mergeLinkSelector).empty
    }

    boolean existsCloneAnotherIntoCurrent(Browser browser) {
        !browser.find(cloneAnotherIntoCurrentLinkSelector).empty
    }

    boolean existsCloneCurrentIntoAnother(Browser browser) {
        !browser.find(cloneCurrentIntoAnotherLinkSelector).empty
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
