package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1469')
@Title('Verify that the history is populated according to activity made on a model')
@Narrative('''
- Login as curator
- Select any Data Model
- Create a data class
- Create a data element
- Edit the created data class and save
- Create a new Tag
''')
@Stepwise
class MET1623Spec extends GebSpec {
    @Shared
    String dataModelName = "TESTING_MODEL"
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
        CreateDataElementPage createDataElementPage = browser.page(CreateDataElementPage)
        createDataElementPage.name = dataElementName
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
        dataElementsPage.hasDataElement(dataElementName)

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