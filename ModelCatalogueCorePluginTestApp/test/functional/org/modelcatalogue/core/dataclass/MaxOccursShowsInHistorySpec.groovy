package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*

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

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
        then:
        at DashboardPage

        /*when: "delete this one"
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select("TESTING_MODEL")
        then:
        at DataModelPage*/
    }

    def "create a data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = "TESTING_MODEL"
        createDataModelPage.description = "TESTING_MODEL_DESCRIPTION"
        createDataModelPage.modelCatalogueIdInput = "KDJFKD9349"
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "create data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Types")
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromPlusButton()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = "TESTING_DATATYPE_TWO"
        createDataTypePage.description = "TESTING_DESCRIPTION"
        createDataTypePage.buttons.save()
        then:
        at DataTypesPage
    }

    def "create data element"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.treeView.select("Data Elements")
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = "TESTING_ELEMENT"
        createDataElementPage.description = "TESTING_DESCRIPTION"
        createDataElementPage.searchMore()
        then:
        at SearchDataTypePage

        when:
        SearchDataTypePage searchDataTypePage = browser.page SearchDataTypePage
        searchDataTypePage.searchDataType("TESTING_DATATYPE")
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
        dataElementsPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = "TESTING_CLASS"
        createDataClassPage.description = "TESTING_DESCRIPTION"
        createDataClassPage.elements()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.searchDataElement("TESTING_ELEMENT")
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "select newly created data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.findByName("TESTING_CLASS")
        then:
        true
    }

}
