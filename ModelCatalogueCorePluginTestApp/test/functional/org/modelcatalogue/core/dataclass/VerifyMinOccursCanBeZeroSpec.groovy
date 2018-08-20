package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1562')
@Title('Verify that min occurs - can enter 0 in the relationship editor')
@Narrative($/
 - Login to Metadata Exchange as supervisor | Login successful
 - Click on the 'Create Data Model' (plus sign) button in the top right hand menu. | Redirected to Create Data Model page
 - Populate form fields with Name, Catalogue ID and description. Click the Save button. | Data Model is created. Redirected to new Data Model main page
 - Navigate by selecting Data Elements tag using tree-navigation panel. | Data Elements page opens in the Display panel. Title is 'Active Data Elements'
 - Click the green 'Create New Element' plus button | Create Data Element pop-up dialogue box appears
 - Fill form with Name, Catalogue ID and type in a new name in the Data Type search bar form field. | Create New and Search options appear as drop-down from Data Type search bar form field
 - Select option to Create New Data Type | Create Data Type pop-up dialogue box appears
 - Fill form with Name, Catalogue ID and select type of Data Type as Simple. Click the Save button. | New Data Type is created. Returned to Create Data Element pop-up dialogue box
 - Verify that new Data Type populates form field for Data Type within Create Data Element pop-up dialogue box. | New Data Type populates Data Type Form field in Create Data Element form.
 - Click the Save button to create Data Element | Data element is created and appears in list under 'Active Data Elements'
 - Navigate to Data Class page by using tree-navigation to select Data Class tag. | Data Class main page opens. Title is 'Active Data Classes'
 - Click on 'Create New Data Class' button - green plus sign. | Create Data Class wizard appears
 - Populate form with Name, Catalogue ID, and description. | Form is populated
 - Click on green tick Save button in top right hand corner of Data Class wizard. | Wait until Data Class has been created, then click the Close button. Data Class is created and is listed under 'Draft Data Classes' page
 - Select Data Class out of list under 'Active Data Classes' | Redirected in display panel to Data Class main page.
 - Select 'Data Class' menu button from top left hand menu. | Data Class menu drop-down appears
 - Select option to 'Create Relationship' . | Create Relationship pop-up dialogue box appears
 - Within "Create Relationship' pop-up dialogue box, select from top (relationship type) drop-down form field the option 'Contains' | Contains is selected in first drop down as relationship type.
 - In Data Element form field, Under title of 'Destination', type name of recently created Data Element. | Data Element name appears in form field under 'Destination'.
 - Underneath 'Destination' click on the caret (arrow) next the title 'Metadata' to expand the Metadata panel. | Metadata panel is expanded within 'Create Relationship' pop-up dialogue box.
 - Select Occurrence tag | Occurrence tab opens out
 - In Min Occurs, enter 0. In Max Occurs, enter 10.  | Numbers entered into Min and Max Occurs
 - Click on the button 'Create Relationship' to create relationship. | Relationship is created. 'Create Relationship' pop-up dialogue box is closed. Refresh Data Class page. Data Element is listed in the 'Data Elements' section of the Data Class main page in the display panel
 - Verify that in the Data Element row, under the Occurs column, the Max and Min Occurrence data is listed. | Max and Min Occurs are listed in line with Data Element data in Data Class main page
/$)

@Stepwise
class VerifyMinOccursCanBeZeroSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelCatalogueId = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "description"
    @Shared
    String dataEleName = UUID.randomUUID().toString()
    @Shared
    String dataEleCatalogueId = UUID.randomUUID().toString()
    @Shared
    String dataTypeElement = UUID.randomUUID().toString()
    @Shared
    String dataTypeName = UUID.randomUUID().toString()
    @Shared
    String dataTypeId = UUID.randomUUID().toString()
    @Shared
    String dataClassName = UUID.randomUUID().toString()
    @Shared
    String dataClassCatalogueId = UUID.randomUUID().toString()
    @Shared
    String dataClassDescription = "description"
    @Shared
    String minOccur = "0"
    @Shared
    String maxOccur = "10"

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "Create data model and filling data model form"() {

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.modelCatalogueId = dataModelCatalogueId
        createDataModelPage.description = dataModelDescription
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "Select and add data elements"() {

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.addItemIcon.click()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataEleName
        createDataElementPage.modelCatalogueId = dataEleCatalogueId
        createDataElementPage.search(dataTypeElement)
        createDataElementPage.createNewItemClick()
        then:
        CreateDataTypePage
    }

    def "Create new data type"() {

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.modelCatalogueId = dataTypeId
        createDataTypePage.simple()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage
    }

    def "Check for new data type created"() {

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        then:
        createDataElementPage.matchSearch(dataTypeElement)

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage
    }

    def "Select data class and create new data class"() {

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.create()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.modelCatalogueId = dataClassCatalogueId
        createDataClassPage.description = dataClassDescription
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }


    def "Open new data class and relationship pop up box"() {

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassLink(dataClassName)
        then:
        at DataClassPage

        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.dataClassDropDownClick()
        dataClassPage.createRelationshipClick()
        then:
        CreateRelationshipPage createRelationshipPage = browser.page CreateRelationshipPage
        sleep(2_000)
        createRelationshipPage.matchPageHeading(dataClassName)

    }

    def "Create relationship"() {

        when:
        CreateRelationshipPage createRelationshipPage = browser.page CreateRelationshipPage
        createRelationshipPage.slectRelationDropdown("contains")
        createRelationshipPage.destinationalue(dataEleName)
        createRelationshipPage.destinationDropdownFirst()
        createRelationshipPage.metadataExpandBttn()
        createRelationshipPage.slectMetadataTab("Occurrence")
        createRelationshipPage.minOccurValue(minOccur)
        createRelationshipPage.maxOccurValue(maxOccur)
        createRelationshipPage.createRelationship()
        then:
        at DataClassPage
    }

    def "verfy for min and max occur in data element row"() {

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassLink(dataClassName)
        then:
        at DataClassPage

        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        then:
        dataClassPage.occuranceStatus().contains("Min Occurs: 0")

        when:
        dataClassPage = browser.page DataClassPage
        then:
        dataClassPage.occuranceStatus().contains("Max Occurs: 10")

        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()
        then:
        at HomePage
    }

}
