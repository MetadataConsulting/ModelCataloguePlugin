package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1564')
@Title('Examine that When remove more than one data element of a class the browser does not crash')
@Narrative($/
 - 1. Login to Metadata Exchange As curator | Login successful
 - 2. Click on the 'Create Data Model' (plus sign) button in the top right hand menu. | Redirected to 'Create Data Model' page.
 - 3. Fill form fields with Name, Catalogue ID, Description and click the Save button. | New Data Model is created. Redirected to new Data Model main page.
 - 4. Using tree-navigation panel, select Data Elements tag to go to Data Elements list page. | Redirected in Display panel to Data Elements list page. Title is 'Active Data Elements'.
 - 5. Click the green 'Create New' plus sign button to create new data element. | Create Data Element pop-up dialogue box appears.
 - 6. Fill form fields with Name, Catalogue ID and Description. In the Data Type form field. type in the name of a new data type. | Drop-down menu appears from search bar with choice to search or create new data type . .
 - 7. Select option from drop-down in Data Type Form field/search bar to Create new Data Type | Create Data Type pop-up dialogue box appears.
 - 8. Fill Create Data Type Form fields with Name, Catalogue ID and Description. Click on the Save button. | New Data Type Created. Create Data Type pop- up closes. New Data type name fills Data Type form field in Create Data Element pop-up dialogue box.
 - 9. In Create Element poop-up dialogue box, click the Save button. | New Data Element is created. Redirected to list in 'Active Data Elements' page.
 - 10. Repeat steps 5 - 9 to create another Data Element | Second new Data Element is created.
 - 11. Select Data Element from list in 'Active Data Elements' list page. | Directed in display panel to Data Element main page
 - 12. Navigate to the 'Data Element' menu button in the top left hand menu. | Data Element menu button drop-down appears
 - 13. Select option to 'Delete' Data Element. | Delete Element pop-up appears.Asking to verify that you want to delete element.
 - 14. Select option OK in Delete Data Element pop up to Delete Data Element | Data Element is deleted. Redirected to main Active Data Elements page.
 - 15. Repeat steps 5 - 9 to create new data element | New Data Element is created
 - 16. Repeat steps 11-14 to Delete a Data Element | Data Element is deleted
 - 17. Repeat steps 15 and 16 five more times. | Check that Metadata Exchange does not crash when Data Elements are repeatedly created and deleted.
/$)
@Stepwise
class AddAndRemoveManyDataElementsSpec extends GebSpec {

    def "login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')
        then:
        at DashboardPage
    }

    def "create new data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = UUID.randomUUID().toString()
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.description = "TESTING_MODEL_DESCRIPTION"
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "create first data element and then delete it"() {
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
        createDataElementPage.name = "TESTING_ELEMENT_ONE"
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = "ELEMENT_ONE_DESCRIPTION"
        createDataElementPage.search("TESTING_DATATYPE_ONE")
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.description = "DATATYPE_ONE_DESCRIPTION"
        createDataTypePage.modelCatalogueId = UUID.randomUUID().toString()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage

        when:
        dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectDataElement("TESTING_ELEMENT_ONE")
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.dataElementDropdown()
        dataElementPage.deleteDataElement()
        dataElementPage.confirmDelete()
        then:
        at DataElementsPage
    }

    def "create second data element and then delete it"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = "TESTING_ELEMENT_TWO"
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = "ELEMENT_TWO_DESCRIPTION"
        createDataElementPage.search("TESTING_DATATYPE_TWO")
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.description = "DATATYPE_TWO_DESCRIPTION"
        createDataTypePage.modelCatalogueId = UUID.randomUUID().toString()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage

        when:
        dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectDataElement("TESTING_ELEMENT_TWO")
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.dataElementDropdown()
        dataElementPage.deleteDataElement()
        dataElementPage.confirmDelete()
        then:
        at DataElementsPage
    }

    def "create third data element and then delete it"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = "TESTING_ELEMENT_THREE"
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = "ELEMENT_THREE_DESCRIPTION"
        createDataElementPage.search("TESTING_DATATYPE_THREE")
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.description = "DATATYPE_THREE_DESCRIPTION"
        createDataTypePage.modelCatalogueId = UUID.randomUUID().toString()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage

        when:
        dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectDataElement("TESTING_ELEMENT_THREE")
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.dataElementDropdown()
        dataElementPage.deleteDataElement()
        dataElementPage.confirmDelete()
        then:
        at DataElementsPage
    }

    def "create fourth data element and then delete it"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = "TESTING_ELEMENT_FOUR"
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = "ELEMENT_FOUR_DESCRIPTION"
        createDataElementPage.search("TESTING_DATATYPE_FOUR")
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.description = "DATATYPE_FOUR_DESCRIPTION"
        createDataTypePage.modelCatalogueId = UUID.randomUUID().toString()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage

        when:
        dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectDataElement("TESTING_ELEMENT_FOUR")
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.dataElementDropdown()
        dataElementPage.deleteDataElement()
        dataElementPage.confirmDelete()
        then:
        at DataElementsPage
    }

    def "create fifth data element and then delete it"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = "TESTING_ELEMENT_FIVE"
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = "ELEMENT_FIVE_DESCRIPTION"
        createDataElementPage.search("TESTING_DATATYPE_FIVE")
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.description = "DATATYPE_FIVE_DESCRIPTION"
        createDataTypePage.modelCatalogueId = UUID.randomUUID().toString()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage

        when:
        createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage

        when:
        dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectDataElement("TESTING_ELEMENT_FIVE")
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.dataElementDropdown()
        dataElementPage.deleteDataElement()
        dataElementPage.confirmDelete()
        then:
        at DataElementsPage
    }

}
