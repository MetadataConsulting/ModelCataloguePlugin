package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1554')
@Title('Examine that when creating a new draft - if there are Data Classes from other models -edits from imported models are updated in your model.')
@Narrative('''
- Login to Model Catalogue As curator
- Navigate to top left hand side menu, select 'create new data model' button ( looks like a black plus sign) to create a draft Model | Directed to Create new Data Model Page
- Fill form with Data Model name, catalogue ID, Description, Click save | Data Model is created and user is taken to Data Model page
- On Data Model page, using the left-hand side tree view navigate to and select "Imported Data Models" | Taken to Data Model (name) Imports page
- Select the green button to import a data model | Imports pop up appears
- In the pop-up, Under title 'Destination', either type the name of a data model in the search bar or select the button (with the book icon) to the left hand side to bring up pop-up box with a list of available data models to import. | Select a DRAFT data model to import
- Select 'Create Relationship ' button to import Data Model | Data Model is imported and shows up in list of imported data models. User is taken back to main page of model
- Navigate to the tree view and click on the Data Classes Link | Active Data Class title is displayed with list of data classes.
- Select the green plus button to create a new data class | Create Data Class wizard/pop-up appears
- Fill form with Data Class Name, Catalogue ID and Description . Click the green save button ( top right) to save Data Class | Data Class is created and appears in list of Data classes
- Click on the newly created data class and from tabs at the bottom of the Data Class info page select 'parent\' | parent tab is opened
- Select the green plus button to add a Data Class parent to the your new data class | Pop up wizard appears with destination form to select parent data class
- Select the book icon to the left of the search bar under title 'Destination' to search for Data Classes from the data model imported into this current one. | pop-up with list of data classes appears
- Add data class from imported model and save | Parent data class relationship is displayed under the parent tab of data class
- Navigate back to home page of the data model and click on data model menu at the top of the page | data model menu drop-down is displayed
- select Finalise Data model | Finalise Data Model pop-up wizard appears
- Fill in revision notes and press finalise button | Data Model is finalised
- In finalised version, navigate to top menu and select 'Data model' button | Data Model drop-down menu shown
- Select 'new version' from drop-down menu to create new version of the flnalised data model | new version pop-up wizard appears
- Fill in new version pop-up wizard semantic version form box with version number for the new version. then select 'create new version' button to create new version.
- While creating a new version tick the checkbox if available (prefer drafts for following dependent data models) | New Version of Data Model is created
- Navigate back to the Data model that you had imported and to the Data Class used in your Data Model. Select the edit button to the top left of the Data Models page. | The page changes to have editable form/text boxes
- Make some small edits to the description or edit the name by clicking the edit button on the top left corner of the data model page and selecting the tick button in its place to save the edits. | edits are saved to the data class used from the imported model.
- Navigate back to your Data Model and to the data class.
- Check that when you search in the parents tab, edits made to the data class in the imported model are carried through to your data model. | Edits are present from imported data model.
''')
@Stepwise
class NewDraftEditFromImportedModelsAreUpdatedSpec extends GebSpec {
    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataClassName = "NEW_TESTING_CLASS"
    @Shared
    String dataClassDescription = "NEW_TESTING_DESCRIPTION"
    @Shared
    String importDataModelName = UUID.randomUUID().toString()
    @Shared
    String importDataClassName = "IMPORT_CLASS"
    @Shared
    String importDataClassNewName = "IMPORT_CLASS_NEW"


    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }


    def "create data model and class to import"() {
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
        dataModelPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = importDataClassName
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "create new data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueId = "${UUID.randomUUID()}"
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "import data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Imported Data Models")
        then:
        at ImportedDataModelsPage

        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.importDataModel()
        then:
        at ImportDataModelPage
    }

    def "search a data model"() {
        when:
        ImportDataModelPage importDataModelPage = browser.page ImportDataModelPage
        importDataModelPage.searchMore()
        then:
        Thread.sleep(1000)
        at SearchModelPage

        when:
        SearchModelPage searchModelPage = browser.page SearchModelPage
        searchModelPage.searchModelByName(importDataModelName)
        then:
        at ImportDataModelPage

        when:
        importDataModelPage = browser.page ImportDataModelPage
        importDataModelPage.createRelationship()
        then:
        at ImportedDataModelsPage
    }

    def "create new data class"() {
        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.treeView.select("Data Classes")

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
        createDataClassPage.description = dataClassDescription
        createDataClassPage.finish()
        createDataClassPage.exit()

        then:
        at DataClassesPage

    }

    def "select newly created data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassByName(dataClassName)
        then:
        at DataClassPage
    }

    def "select parents"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.selectParents()
        dataClassPage.addParent()
        then:
        at ParentClassModalPage

        when:
        ParentClassModalPage parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.searchMore()
        Thread.sleep(1500)
        then:
        at SearchClassPage

        when:
        SearchClassPage searchClassPage = browser.page SearchClassPage
        searchClassPage.search(importDataClassName)
        then:
        at ParentClassModalPage

        when:
        parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.createRelationship()
        then:
        at ParentClassModalPage
    }

    def "finalize the data model"() {
        when:
        ParentClassModalPage parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.openModelHome(dataModelName)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.finalizedDataModel()
        then:
        waitFor(10) { at FinalizeDataModelPage }

        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.versionNote = "version information"
        finalizeDataModelPage.submit()
        then:
        at DataModelFinalizeConfirmPage

        when:
        DataModelFinalizeConfirmPage dataModelFinalizeConfirmPage = browser.page DataModelFinalizeConfirmPage
        dataModelFinalizeConfirmPage.hide()
        then:
        at DataModelPage
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
        dataModelAssignNewVersionPage.semanticVersion = "0.0.2"
        dataModelAssignNewVersionPage.createNewVersion()
        then:
        at DataModelAssignNewVersionConfirmPage

        when:
        DataModelAssignNewVersionConfirmPage dataModelAssignNewVersionConfirmPage = browser.page DataModelAssignNewVersionConfirmPage
        dataModelAssignNewVersionConfirmPage.hide()
        then:
        at DataModelPage
    }

    def "update name of data class of imported data model"() {

        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.search(importDataModelName)
        dashboardPage.select(importDataModelName)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassByName(importDataClassName)
        then:
        at DataClassPage

        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.edit()
        dataClassPage.editClassName(importDataClassNewName)
        dataClassPage.save()
        then:
        at DataClassPage
    }

    def "open new version of data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.selectModelByNameAndIndex(dataModelName, 1)
        then:
        at DataModelPage

    }

    def "check imported data class name is changed here also"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Imported Data Models")
        then:
        at ImportedDataModelsPage

        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.selectModelByName(importDataModelName)
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        then:
        dataClassesPage.containsDataClass(importDataClassNewName)
    }

}
