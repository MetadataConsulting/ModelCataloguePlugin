package org.modelcatalogue.core.datatype

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*
import spock.lang.Shared

@Issue('https://metadata.atlassian.net/browse/MET-1507')
@Title('New Version - Check new data Type added')
@Narrative('''
 - Login to Metadata Exchange As admin | Login successful
 - Click on the 'Create New Data Model' button ( Plus sign) in the top right hand menu | Redirected to Create New Data Model page
 - Fill in the Name, Catalogue ID and Description and click on the 'Save' button to create Data model | Data Model is created, Redirected to new data model main page.
 - Navigate using the tree - navigation panel and click on the Data Types tag | Display panel opens up the Data Types main page. Check that title is "Active Data Types"
 - On the display panel side, click on the green plus button to create a new data type | Create Data Type pop-up wizard opens
 - Fill the form with name, description and type ( select simple) and click on the Save button | Data type is created and is present in list under 'Active Data Types'.
 - Navigate back to the Data Model's main page by selecting the Data Model name in the tree-navigation panel. | Main page of Data Model is displayed.
 - Then select the Data Model menu button in the top left hand menu. | Data Model menu drop-down appears
 - Select 'Finalize' from the drop-down list to finalize the data model | Finalize data model pop-up dialogue box appears
 - Fill the form with the semantic version number and revision notes. Click the Finalize button | May see a Finalizing Data Model pop-up. If so, click Hide. Data Model is finalized. Redirected to finalized data model main page
 - Verify that the data model is finalized. | Check that the Activity tab contains model version and finalized eg: test7(0.0.1) finalized
 - Navigate to the top menu and click on the Data model button in the top left hand menu. | Data Model drop-down menu appears
 - Select the option New Version to create a new draft version of the data model | 'New Version of Data Model' pop-up dialogue box appears
 - Enter new version into Semantic Version field in the dialogue box | New Semantic Version number is entered
 - Click on the Create New Version button | Create new version of Data Model pop-up may appear. If so, keep checking the text content until it ends with 'COMPLETED SUCCESSFULLY'. Click Hide. New Version is created and redirected to data model main page
 - Navigate to the tree view and click on the versions tag to open up the list | Listed versions display both finalized and draft data model
 - Select the draft data model from the options under the versions tag | Check the title in the display panel is the name of the Data Model
 - On the display panel on the right side, scroll down to the activity tab | It should say something like 'New version [data model name] (0.0.2) created
 - From the activities tab, Select name ( which is a link) of the data model | Directed to the new draft data model main page
 - From the draft Data Model main page, Navigate down the tree structure within the tree view and click on the Data Types tag to open up Data Types main page | Data Types main page is open. Check that title is "Active Data Types"
 - Select the 'Create new data type' green plus button | Create Data Type pop-up dialogue box appears
 - Enter Name, Catalogue ID, Description and select type of data type. Click Save | New Data Type is created.
 - Check that new data type appears under list in Data Types main page ( 'Active Data types as title) | Data Type has been created
''')
@Stepwise
class CheckDataTypeAddedToNewVersionSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString() // "TESTING_MODEL"
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataTypeName = UUID.randomUUID().toString() // "TESTING_DATATYPE"
    @Shared
    String dataTypeDescription = "TESTING_DATATYPE_DESCRIPTION"
    @Shared
    String dataModelVersion = "0.0.2"
    @Shared
    String dataModelVersionNote = "FINALIZING_DATAMODEL"
    @Shared
    String dataModelNewVersion = "0.0.3"
    @Shared
    String dataTypeTwoName = UUID.randomUUID().toString() // "TESTING_DATATYPE_TWO"
    @Shared
    String dataTypeTwoDescription = "TESTING_DATATYPE_TWO_DESCRIPTION"

    def "Login as admin"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("admin", "admin")
        then:
        at DashboardPage
    }

    def "create a data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "create a data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataTypes()
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromGreenPlusButton()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = dataTypeName
        createDataTypePage.description = dataTypeDescription
        createDataTypePage.buttons.save()
        then:
        at DataTypesPage
    }

    def "navigate back to data model main page"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.treeView.dataModel()
        then:
        at DataModelPage
    }

    def "finalize the data model"() {
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
        Thread.sleep(1000)
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }

    def "verify data model is finalized"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        true
        dataModelPage.isDataModelFinalized()
    }

    def "create new version"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdownMenu.createNewVersion()
        then:
        at CreateDataModelNewVersionPage

        when:
        CreateDataModelNewVersionPage createDataModelNewVersionPage = browser.page CreateDataModelNewVersionPage
        createDataModelNewVersionPage.newVersion = dataModelNewVersion
        createDataModelNewVersionPage.createNewVersion()
        Thread.sleep(1000)
        then:
        at CreatedDataModelNewVersionPage

        when:
        CreatedDataModelNewVersionPage createdDataModelNewVersionPage = browser.page CreatedDataModelNewVersionPage
        Thread.sleep(1000)
        createdDataModelNewVersionPage.hide()
        Thread.sleep(2000)
        then:
        at DataModelPage
    }

    def "navigate to new version created"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.versions()
        then:
        at VersionsPage

        when:
        VersionsPage versionsPage = browser.page VersionsPage
        driver.navigate().refresh()
        versionsPage.selectModelByVersion(dataModelNewVersion)
        then:
        at DataModelPage
    }

    def "create new data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataTypes()
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromGreenPlusButton()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = dataTypeTwoName
        createDataTypePage.description = dataTypeTwoDescription
        createDataTypePage.buttons.save()
        then:
        at DataTypesPage
    }

    def "verify new data type is created"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        then:
        dataTypesPage.hasDataType(dataTypeTwoName)
    }
}
