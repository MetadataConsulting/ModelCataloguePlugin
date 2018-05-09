package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import spock.lang.Shared
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1496')
@Title('Examine that user can create data elements on the fly from data class wizard')
@Narrative($/
 - Login to Metadata Exchange As curator | Login successful
 - From top-right hand menu, select the 'Create Data Model' button (plus sign) | Redirected to 'Create Data Model' page
 - Fill in form with Name, Catalogue ID, Description. Press the Save button | New data model is created. Redirected the new data model's main page
 - Using the tree-navigation panel, navigate and select Data Classes . | Display panel on right opens up Data Classes main page. Title is 'Active Data Classes'
 - Select the (create) New Data Class button in the menu on the left side. | 'Data Class Wizard' pop-up dialogue box appears
 - Fill the form with Name, Catalogue ID and Description | Form fields are filled
 - From the tabs in the top of the dialogue box, select 'Elements' | Elements section of the Data Class Wizard pop-up appears.
 - In the search bar (underneath the title 'Data Elements'), type in a name of a new Data Element. | Drop-down menu appears from search bar
 - From drop-down menu, select option to 'Create new' Data Element | 'Create Data Element' pop-up dialogue box appears
 - Fill 'Create Data Elements' form with Name, Catalogue ID and description. Press Save button. | Form fields are filled. New Data Element is created. New data element name is listed under 'Data Elements' title in Data Class Wizard.
 - In the search bar (underneath the title 'Data Elements'), type in a name of a new Data Element. | Drop-down menu appears from search bar
 - Ignore drop down. Instead click on Green plus-sign button to the right of the search bar. | New data element is created immediately. Data Element name is shown under 'Data Element' title ( and above the 'Data Elements' search bar in Data Class Wizard.
 - In the Data Class Wizard, select the green tick button to Save | New Data Class is created. Data Class Wizard presents option to Close or Create Another Data Class.
 - Select Close button in Data Class Wizard to close the Wizard. | Directed to Data Classes main page. New Data Class is listed
 - Verify that newly created Data Class is listed in data class main page under 'Active Data Classes' | Data Class is listed.
 - Select the newly created Data Class | Directed to Data Class main page within display panel.
 - Check that both the data element is listed under 'Data Elements' section within the Data Class display panel . | Data Elements are listed.
 - Within the tree-navigation panel, navigate and select 'Data Elements' tag | Display panel opens up Data Elements main page. Page title is 'Active Data Elements'
 - Verify that in list under 'Active Data Elements' that the newly created data elements are displayed | Data Elements are displayed
/$)

@Stepwise
class CheckCreateElementFromClassWizardSpec extends GebSpec {

    @Shared
    String dataModelName = "TESTING_MODEL"
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataClassName = "TESTING_CLASS"
    @Shared
    String dataClassDescription = "TESTING_CLASS_DESCRIPTION"
    @Shared
    String dataElementName = "TESTING_ELEMENT_ONE"
    @Shared
    String dataElementDescription = "TESTING_ELEMENT_DESCRIPTION"
    @Shared
    String dataElementTwoName = "TESTING_ELEMENT_TWO"

    def "login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
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

    def "go to data classes"() {
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
        createDataClassPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataClassPage.description = dataClassDescription
        createDataClassPage.elements()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.dataElement = dataElementName
        createDataClassPage.createNewElement()
        then:
        at CreateDataElementPage
    }

    def "create new data element"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = dataElementDescription
        createDataElementPage.finish()
        then:
        at CreateDataClassPage
    }

    def "create another data element and save data class"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.dataElement = dataElementTwoName
        createDataClassPage.createNewElementFromPlusButton()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "verify newly created data class is present"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        then:
        dataClassesPage.containsDataClass(dataClassName)
    }

    def "select newly creted data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClass(dataClassName)
        then:
        at DataClassPage
    }

    def "verify newly created data elements are present in data class"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.treeView.dataElements()
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        then:
        dataElementsPage.containsElement(dataElementName)
        dataElementsPage.containsElement(dataElementTwoName)
    }

}
