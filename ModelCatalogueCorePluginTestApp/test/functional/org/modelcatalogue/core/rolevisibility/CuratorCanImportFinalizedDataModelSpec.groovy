package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title
import spock.lang.Stepwise
import spock.lang.Shared
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1626')
@Title('Check that a curator can Add Data Model Import from Data Model menu')
@Narrative($/
 - 1. Login to Metadata Exchange as curator | Login successful
 - 2. Click on the 'Create Data Model' button (plus sign) in the top right menu | Redirected to 'Create Data Model' page
 - 3. Fill form with Name, Catalogue ID, Description and click the Save button. | New Data Model is created. Redirected to new Data Models main page in display panel.
 - 4. Using tree-navigation panel, select Data Classes tag. | Redirected in display panel to Active Data Classes page. Title should be 'Active Data Classes'.
 - 5. Click on the Create New (green plus sign) button in the display panel under the 'Active Data Classes' title. | Create new Data Class Wizard pop-up appears. Title is 'Data Class Wizard'.
 - 6. Fill Data Class Wizard form with Name, Catalogue ID , description. and click the green save button ( green tick) in the top right hand of the Data Class Wizard. | Data Class is created. Data Class Wizard gives options to Create Another or Close.
 - 7. Select to close Data Class Wizard. | Data Class Wizard is closed. New Data Class is present in list under 'Active Data Classes' .
 - 8. Select Data Model tag in tree-navigation panel. Select 'Data Model' menu button from top left hand menu and select 'Finalize' option | Finalize model dialogue pop-up appears
 - 9. Fill in 'Finalize Data Model' pop-up with Semantic Version number and revision notes. Click on 'Finalize' button | 'Finalizing' process dialogue box appears
 - 10. Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Click 'Hide' on the 'Finalizing' process box | Data Model is finalized. Redirected to main page of Finalized data model in both display and tree navigation panel
 - 11. Go to Dashboard page. Repeat steps 2-7 | Second Data Model is created
 - 12. Select Data Model tag in tree-navigation panel | In main page of second Data Model
 - 13. Select the 'Data Model' menu button from the top left hand menu | 'Data Model' menu button drop-down appears
 - 14. Select option to 'Add Data Model Import' from drop-down menu | Add Data Model Import pop-up dialogue box appears
 - 15. In search bar, type the name of the first Data Model created. Select the first data model from the list | First Data Model appears above form field of Add Data Model Import pop-up dialogue box.
 - 16. Click the OK button to import Data Model. | Data Model is imported. Add Data Model Import pop-up dialogue box closes. Reverts to Data Model main page.
 - 17. Select 'Imported Data Models' tag using tree-navigation panel. | Imported Data Models tag opens. Display panel is directed to '[Data model name] Imports' page.
 - 18. Verify that imported Data model name is listed under '[Data model name] Imports' page. | Imported Data Model is listed.
/$)

@Stepwise
class CuratorCanImportFinalizedDataModelSpec extends GebSpec {

    @Shared
    String dataModelOneName = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataModelTwoName = UUID.randomUUID().toString()
    @Shared
    String dataClassName = "DATA_CLASS"
    @Shared
    String dataClassId = "ID23254"
    @Shared
    String dataClassDescription = "Data class description"
    @Shared
    String versionNote = "DATA MODEL FINALIZED"
    @Shared
    String dataModelId = "MT-234567"

    def "login to curator"() {
        when:
        sleep(3_000)
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "create new data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.with {
            name = dataModelOneName
            modelCatalogueId = "MT-234"
            description = dataModelDescription
            submit()
        }
        then:
        at DataModelPage
    }

    def "navigate to data classes"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
        then:
        at DataClassesPage
    }

    def "create new data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.modelCatalogueId = dataClassId
        createDataClassPage.description = dataClassDescription
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "finalize data model"() {
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
        finalizeDataModelPage.versionNote = versionNote
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }

    def "create another data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.with {
            name = dataModelTwoName
            modelCatalogueId = dataModelId
            description = dataModelDescription
            submit()
        }
        then:
        at DataModelPage
    }

    def "import data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdownMenu.addImport()
        then:
        at DropDownImportPage

        when:
        DropDownImportPage dropDownImportPage = browser.page DropDownImportPage
        dropDownImportPage.fillSearchBox(dataModelOneName)
        dropDownImportPage.searchMore()
        then:
        at SearchTagPage

        when:
        SearchTagPage searchTagPage = browser.page SearchTagPage
        searchTagPage.searchTag(dataModelOneName)
        then:
        at DropDownImportPage

        when:
        dropDownImportPage = browser.page DropDownImportPage
        Thread.sleep(1000)
        dropDownImportPage.finish()
        then:
        at DataModelPage
    }

    def "verify data model is imported"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.importedDataModels()
        then:
        at DataImportsPage

        when:
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        driver.navigate().refresh()
        then:
        dataImportsPage.containsDataModel(dataModelOneName)
    }
}