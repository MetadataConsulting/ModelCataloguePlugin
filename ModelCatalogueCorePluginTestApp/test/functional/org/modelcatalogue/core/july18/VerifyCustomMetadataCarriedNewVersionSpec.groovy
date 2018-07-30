package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1437')
@Title('Verify Custom Metadata carried new version')
@Stepwise
class VerifyCustomMetadataCarriedNewVersionSpec extends GebSpec {

    @Shared
    String datamodelName = UUID.randomUUID().toString()
    @Shared
    String datamodelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataModelID = UUID.randomUUID().toString()
    @Shared
    String dataClassID = UUID.randomUUID().toString()
    @Shared
    String dataElementID = UUID.randomUUID().toString()
    @Shared
    String dataClassName = "Data class Name"
    @Shared
    String dataElementDescription = "Data element description"
    @Shared
    String dataClassDescription = "Data class description"
    @Shared
    String dataTypeDescription = "Data type description"
    @Shared
    String dataElementName = "Data element Name"
    @Shared
    String dataTypeName = "Data type Name"

    @Shared
    String datamodelName2 = UUID.randomUUID().toString()
    @Shared
    String datamodelDescription2 = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataModelID2 = UUID.randomUUID().toString()
    @Shared
    String dataClassID2 = UUID.randomUUID().toString()
    @Shared
    String dataElementID2 = UUID.randomUUID().toString()
    @Shared
    String dataClassName2 = "Data class Name"
    @Shared
    String dataElementDescription2 = "Data element description"
    @Shared
    String dataClassDescription2 = "Data class description"
    @Shared
    String dataTypeDescription2 = "Data type description"
    @Shared
    String dataElementName2 = "Data element Name"
    @Shared
    String dataTypeName2 = "Data type Name"


    def "login as supervisor"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')
        then:
        at DashboardPage
    }

    def "create data model and data class"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = datamodelName
        createDataModelPage.modelCatalogueIdInput = dataModelID
        createDataModelPage.description = datamodelDescription
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        sleep(2_000)
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.modelCatalogueId = dataClassID
        createDataClassPage.description = dataClassDescription
        createDataClassPage.elements()
        createDataClassPage.setDataElement(dataElementName)
        createDataClassPage.createNewElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.setDescription(dataElementDescription)
        createDataElementPage.setModelCatalogueId(dataElementID)
        createDataElementPage.search(dataTypeName)
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.setDescription(dataTypeDescription)
        createDataTypePage.simple()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.verifyDataElementAdded(dataElementName)
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }


    def "create second data model, data class, datatype and data element"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = datamodelName2
        createDataModelPage.modelCatalogueIdInput = dataModelID2
        createDataModelPage.description = datamodelDescription2
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        sleep(2_000)
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName2
        createDataClassPage.modelCatalogueId = dataClassID2
        createDataClassPage.description = dataClassDescription2
        createDataClassPage.elements()
        createDataClassPage.setDataElement(dataElementName2)
        createDataClassPage.createNewElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page(CreateDataElementPage)
        createDataElementPage.setDescription(dataElementDescription2)
        createDataElementPage.setModelCatalogueId(dataElementID2)
        createDataElementPage.search(dataTypeName2)
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.setDescription(dataTypeDescription2)
        createDataTypePage.simple()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page(CreateDataElementPage)
        createDataElementPage.finish()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page(CreateDataClassPage)
        createDataClassPage.verifyDataElementAdded(dataElementName2)
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "select scond data model and finalize"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.treeView.dataModel()
        then:
        at DataModelPage
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.finalizedDataModel()
        then:
        at FinalizeDataModelPage

        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.version = "0.0.2"
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page(DataModelPage)
        dataModelPage.clickDataExchangeIcon()
        then:
        at DashboardPage
    }

    def "select finalize data model verify version number and status of finalized data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search(datamodelName2)
        dashboardPage.select(datamodelName2)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        dataModelPage.checkFinalizedStatus()
        when:
        dataModelPage = browser.page DataModelPage
        then:
        dataModelPage.verifySemanticNumber("0.0.2")
    }

    def "create new version"() {

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dataModel()
        dataModelPage.dropdownMenu.createNewVersion()
        then:
        at DataModelAssignNewVersionPage

        when:
        DataModelAssignNewVersionPage dataModelAssignNewVersionPage = browser.page DataModelAssignNewVersionPage
        dataModelAssignNewVersionPage.semanticVersion = "0.0.3"
        dataModelAssignNewVersionPage.createNewVersion()
        then:
        at DataModelAssignNewVersionConfirmPage

        when:
        DataModelAssignNewVersionConfirmPage dataModelAssignNewVersionConfirmPage = browser.page DataModelAssignNewVersionConfirmPage
        dataModelAssignNewVersionConfirmPage.hide()
        then:
        at DataModelPage
    }

    def "verify new version created"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        sleep(2_000)
        dashboardPage.search(datamodelName2)
        dashboardPage.select(datamodelName2)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.versions()
        then:
        at VersionsPage

        when:
        VersionsPage versionsPage = browser.page VersionsPage
        then:
        versionsPage.verifyVersionCreated("0.0.3")
    }
}