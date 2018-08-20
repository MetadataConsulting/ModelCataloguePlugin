package org.modelcatalogue.core.july18

import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1477')
@Title('Verify that connection lost message is not displayed constantly')
@Stepwise
class SearchingDataModelsResultShouldDisplayedWithVersionsSpec extends AbstractModelCatalogueGebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString() + "data type name"
    @Shared
    String importDataModelName = UUID.randomUUID().toString() + "data model name"
    @Shared
    String dataTypeID = UUID.randomUUID().toString()
    @Shared
    String dataTypeDescription = "dataTypeDescription"
    @Shared
    String dataModelVersion = "0.0.3"
    @Shared
    String dataModelVersionNote = "Version finalized"

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "create data model and data type"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = importDataModelName
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataTypes()
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromPlusButton()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = dataTypeName
        createDataTypePage.modelCatalogueId = dataTypeID
        createDataTypePage.description = dataTypeDescription
        createDataTypePage.buttons.save()
        then:
        at DataTypesPage
    }

    def "finalize the data model"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.treeView.dataModel()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.finalizedDataModel()
        then:
        at FinalizeDataModelPage

        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.version = dataModelVersion
        finalizeDataModelPage.versionNote = dataModelVersionNote
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }

    def "deprecate data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataTypes()
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.selectDataType(dataTypeName)
        then:
        DataTypePage

        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        sleep(2_000)
        dataTypePage.selectDataTypeDropdown()
        dataTypePage.clickDeprecate()
        dataTypePage.confirmDeprecate()
        then:
        at DataTypePage
    }

    def "Select data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.search("Test 2")
        dashboardPage.select("Test 2")
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdownMenu.addImport()
        then:
        at DropDownImportPage

        when:
        DropDownImportPage dropDownImportPage = browser.page DropDownImportPage
        dropDownImportPage.fillSearchBox(importDataModelName)
        dropDownImportPage.searchMoreIconButton()
        then:
        at SearchTagPage

        when:
        SearchTagPage searchTagPage = browser.page SearchTagPage
        searchTagPage.searchTag(importDataModelName)
        then:
        at DropDownImportPage

        when:
        dropDownImportPage = browser.page DropDownImportPage
        sleep(1000)
        dropDownImportPage.finish()
        then:
        at DataModelPage
    }

    def "data element"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataElements()
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = "data element new name"
        createDataElementPage.searchMore()
        createDataElementPage.showAllDataType()
        then:
        at SearchAllModalPage

        when:
        SearchAllModalPage searchAllModalPage = browser.page SearchAllModalPage
        then:
        searchAllModalPage.selectDataTypeText(dataTypeName, dataModelVersion)
    }
}