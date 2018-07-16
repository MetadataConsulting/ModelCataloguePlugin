package org.modelcatalogue.core.crud

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1623')
@Title('Check that a user can create data type from data element')
@Narrative('''
- Login to model catalogue as curator/curator
- Create data model - Fill the form with random Data Model Name
- Select Data Elements in the tree and Create a new Data Element. 
- During the creation of the Data Element (on the bottom input field), write a non existing data type name and click create new.
- Create Data Type Pop-up  Wizard appears. Fields are populated with name of data type. Click save-
- Data Type is created and populated in the data element form.  
- Click save in the data element form
- Verify the data model has a data element and a data type
- Verify that Data Element is related to the Data Type (visiting the data element detail page shows a link to the data type)
''')
@Stepwise
class CanCreateDataTypeFromCreateDataElementWizardSpec extends GebSpec {
    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataTypeName = "TESTING_DATATYPE"
    @Shared
    String dataElementName = "TESTING_ELEMENT"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "create data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.submit()

        then:
        Thread.sleep(3000)
        at DataModelPage
    }

    def "create data element"() {
        when:
        DataModelPage dataModelPage = browser.page(DataModelPage)
        dataModelPage.treeView.select('Data Elements')
        then:
        at DataElementsPage
    }

    def "navigate to data element creation page"() {
        when:
        DataElementsPage dataElementsPage = browser.page(DataElementsPage)
        dataElementsPage.createDataElement()

        then:
        at CreateDataElementPage
    }

    def "fill the create data element form"() {
        when:
        when:
        CreateDataElementPage createDataElementPage = browser.page(CreateDataElementPage)
        createDataElementPage.name = dataElementName
        sleep(2_000)
        createDataElementPage.search(dataTypeName)

        then:
        at CreateDataElementPage
    }

    def "create data type"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.createDataType()
        then:
        at DataElementsPage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage
    }

    def "save data element"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage
    }

    def "verify data model has data element and data type"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        then:
        waitFor(10) { dataElementsPage.hasDataElement(dataElementName) }

        when:
        dataElementsPage = browser.page DataElementsPage
        dataElementsPage.treeView.select('Data Types')
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        then:
        dataTypesPage.hasDataType(dataTypeName)
    }

    def "verify data element is related to data type"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.treeView.select('Data Elements')

        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectRow(0)

        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        then:
        dataElementPage.containsDataType(dataTypeName)
    }
}
