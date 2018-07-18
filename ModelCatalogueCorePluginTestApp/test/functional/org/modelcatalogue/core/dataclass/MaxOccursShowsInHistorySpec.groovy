package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1475')
@Title('Max Occurs is showing in History')
@Narrative('''
 - Login to Metadata Exchange As Curator | Login successfuly
 - On Metadata Exchange home page select 'Create New Data Model' ( plus sign) button from top right hand menu | Redirected to Create New Data Model page
 - Fill the form with Name, Catalogue ID, Description and press Save button | New Data Model is created . Redirected to main page of new data model
 - Navigate via tree-panel on the left and select Data Types tag. | Main Data types page opens in Display panel. Title is 'Active Data Types'
 - Select the green plus button under the title (in the display panel) to create a new data type | Create Data Type Pop-up dialogue box appears
 - Fill in form with Data Type name, catalogue ID, Description and select type of data type. Press save. | New data type is created and listed under 'Active Data Types'
 - Navigate via tree panel and select Data Elements tag. | Data Elements main page opens in the display panel. Title is 'Active Data Elements'
 - Select the green plus button under the title (in the display panel) to create a new data Element | Create Data Element Pop-up dialogue box appears
 - Fill in form with Data Element name, catalogue ID, description and in the Data Type form field, write/select the name of the Data Type recently created. | Name of recently created data type appears in drop down below Data Type selection form field.
 - Select name of Data Type recently created from drop-down. Verify that it appears in Data Type selection form field. Press save. | New Data Element is created with selected Data Type. Verify in Data Element main page.
 - Navigate via tree-panel and select Data Classes tag. | Main Data Class page opens. Title is 'Active Data Classes'
 - Select the green plus button under the title (in the display panel) to create a new Data Class | Data Class Wizard pop-up dialogue box appears
 - Fill form with Name, Catalogue ID and description. In section called elements, write name of recently created Data Element | Data Element name appears in drop down
 - Select data element from drop down. Press green save button (with tick symbol) in top right corner of pop-up dialogue box. The click the close button. | New data class is created. Data class is listed under 'Draft Data Classes'.
 - Select the name of the recently created Data Class | Directed to Data Class main page in the display panel.
 - Navigate to top left hand menu and select Data Class menu button. | Data Class menu drop-down appears
 - Select 'Create Relationship ' option from drop down | 'Create Relationship' pop-up dialogue box appears.
 - At the top of the dialogue box, expand the drop down menu and choose relationship type from selection | Drop down appears with list of types of relationships
 - Select either child of or parent of. | Child of / Parent of populates form field. Below more options become present.
 - Under title Destination, select the Data Class icon to the left of the form field to open up an 'import data class' dialogue box. | import data class dialogue box appears
 - Within dialogue box, under the 'search for Data Classes' form field, select the option to 'Add Import' | New search dialogue box appears. Title is 'Add Data Model Import'
 - In the 'Add Data Model Import' dialogue box. select the book icon to the left of the form field to bring up a new dialogue box with list of Data Models. | New dialogue box with list of Data Models appears.
 - Select a data model from the list and press ok in the 'Add Data Model Import' dialogue box' | Back to the original 'Search for Data Class' search dialogue box.
 - In the search box type in a name of a data class within the data model you've just imported | List of possible data class names appear in drop down below search form field.
 - Select one of the Data Classes from list | Data Class populates 'Destination' field in original 'Create New Relationship' dialogue box.
 - Below destination field, select caret ( arrow) next to title Metadata, to expand the metadata section | Metadata section is expanded
 - Select 'Occurence' from Metadata Section | Occurrence form fields open in dialogue box. Min occurs and Max Occurs form fields are present.
 - Populate Max Occurs to any valid value, e.g. 10 | Max occurs is populated
 - Click on the Create Relationship button | New relationship is created. Directed back to Data Class main page in display panel.
 - Navigate to History tab within data class main display panel
 - Check that new relationship created is referenced within history. | History is populated with details of new relationship
 - Verify that included in the history is mention of Max occurs created. like the following: Relationship testclass (AA_test 0.0.2) parent of HAEMATOLOGY - LABORATORY RESULTS - AML, ALL, HODGKIN (Cancer Outcomes and Services Dataset 6.0.0) metadata Max Occurs created | Max Occurs is showing in history
''')
@Stepwise
class MaxOccursShowsInHistorySpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataTypeName = UUID.randomUUID().toString()
    @Shared
    String dataTypeDescription = "TESTING_DATATYPE_DESCRIPTION"
    @Shared
    String dataElementName = UUID.randomUUID().toString()
    @Shared
    String dataElementDescription = "TESTING_DATAELEMENT_DESCRIPTION"
    @Shared
    String dataClassName = UUID.randomUUID().toString()
    @Shared
    String dataClassDescription = "TESTING_DATACLASS_DESCRIPTION"
    @Shared
    String searchDataModelName = UUID.randomUUID().toString()
    @Shared
    String searchDataClassName = "IMPORT_CLASS"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
        then:
        at DashboardPage
    }

    def "create import data model and finalize it"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = searchDataModelName
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = searchDataClassName
        createDataClassPage.description = dataClassDescription
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage


        when:
        dataClassesPage = browser.page DataClassesPage
        dataClassesPage.treeView.dataModel()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.finalizedDataModel()
        then:
        at FinalizeDataModelPage
        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.setVersionNote("finalizing data model")
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage
        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }

    def "create a data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
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

    def "create data type"() {
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
        createDataTypePage.description = dataTypeDescription
        createDataTypePage.buttons.save()
        then:
        at DataTypesPage
    }

    def "create data element"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.treeView.dataElements()
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataElementName
        createDataElementPage.description = dataElementDescription
        createDataElementPage.searchMore()
        then:
        at SearchDataTypePage

        when:
        SearchDataTypePage searchDataTypePage = browser.page SearchDataTypePage
        searchDataTypePage.searchDataType(dataTypeName)
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage
    }

    def "create data class"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.treeView.dataClasses()
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
        createDataClassPage.elements()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.searchDataElement(dataElementName)
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "select newly created data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.findByName(dataClassName)
        then:
        at DataClassPage
    }

    def "select create relationship option"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.selectDataClassDropdown()
        then:
        at DataClassPage

        when:
        dataClassPage = browser.page DataClassPage
        dataClassPage.selectCreateRelationship()
        then:
        at CreateRelationshipPage

        when:
        CreateRelationshipPage createRelationshipPage = browser.page CreateRelationshipPage
        createRelationshipPage.selectRelationshipType("parent of")
        createRelationshipPage.searchMore()
        then:
        at SearchDataClassPage
    }

    def "create relationship"() {
        when:
        SearchDataClassPage searchDataClassPage = browser.page SearchDataClassPage
        searchDataClassPage.addImport()
        then:
        at AddDataModelImportPage

        when:
        AddDataModelImportPage addDataModelImportPage = browser.page AddDataModelImportPage
        addDataModelImportPage.searchMore()
        then:
        at SearchDataModelPage

        when:
        SearchDataModelPage searchDataModelPage = browser.page SearchDataModelPage
        searchDataModelPage.searchDataModel(searchDataModelName)
        then:
        at AddDataModelImportPage

        when:
        addDataModelImportPage = browser.page AddDataModelImportPage
        addDataModelImportPage.importDataModel()
        then:
        at SearchDataClassPage

        when:
        searchDataClassPage = browser.page SearchDataClassPage
        searchDataClassPage.searchDataClass(searchDataClassName)
        then:
        at CreateRelationshipPage
    }

    def "finish creating relationship"() {
        when:
        CreateRelationshipPage createRelationshipPage = browser.page CreateRelationshipPage
        createRelationshipPage.openMetadata()
        then:
        at CreateRelationshipPage

        when:
        createRelationshipPage = browser.page CreateRelationshipPage
        createRelationshipPage.openOccuranceNavigator()
        createRelationshipPage.maxOccurance = 10
        then:
        at CreateRelationshipPage

        when:
        createRelationshipPage = browser.page CreateRelationshipPage
        createRelationshipPage.createRelationship()
        driver.navigate().refresh()
        then:
        at DataClassPage
    }

    def "verify created relationship is show in history section"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage

        String text = dataClassPage.historyChange(0)
        then:
        assert text.contains(searchDataModelName)
        assert text.contains("Max Occurs")

        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()
        then:
        at HomePage
    }


}
