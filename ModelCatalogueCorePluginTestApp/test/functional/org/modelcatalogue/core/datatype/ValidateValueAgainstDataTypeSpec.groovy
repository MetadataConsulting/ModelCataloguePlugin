package org.modelcatalogue.core.datatype

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1722')
@Title('Validate Value against Data Type')
@Narrative('''
- Login to model catalogue as curator
- Select Create New Data Model button ( black plus sign) from the top right menu to create new data model. | Redirect to Create New Data Model Page
- Populate form with data Model name, catalogue ID and Description . Click save | Data Model is created . Directed to Data Model page.
- Navigate using tree view to data type . | Active Data Types page is displayed
- Press green plus button to create new data type | Create New Data Type Pop-up Wizard appears
- Populate form with data type name, catalogue id and description. Select Enumerated option from the radio button options at the bottom of the form. | Form expands to show inputs for key and description for enumeration type.
- populate key and description for data type . press the plus button to make more then one enumeration. Click save | new data type is created.
- If you are not on the 'Active Data Types page' .Navigate to the tree view | Active Data Types page is open
- Select the newly created enumerated data type from the list of data types | The enumerated data type page is open with its name as the title.
- Navigate to the top menu and click on the Enumerated Type menu button | drop-down menu appears
- Select Validate Value | Validate Value by data type pop up page is opened
- In the Value form-box type a value that is correct | confirm that it shows as valid
- In the Value form-box type a value that is incorrect | confirm that it shows as invalid
- check that incorrect value shows INVALID and | correct value shows VALID
''')
@Stepwise
class ValidateValueAgainstDataTypeSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataTypeName = "TESTING_DATATYPE"

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

    def "create data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        waitFor(30) { at DataTypesPage }

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage
    }

    def "fill data type values"() {
        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeName
        createDataTypePage.enumerated()
        createDataTypePage.fillMetadata(one: 1)
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "select newly created data type"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.selectDataType(dataTypeName)
        then:
        waitFor(5) {at DataTypePage}
    }

    def "select validate value"() {
        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        dataTypePage.enumeratedType()
        dataTypePage.validateValue()
        then:
        at DataTypeValueValidatePage
    }

    def "validate values"() {
        when:
        DataTypeValueValidatePage dataTypeValueValidatePage = browser.page DataTypeValueValidatePage
        dataTypeValueValidatePage.validateKeyField = "one"

        then:
        waitFor(5) { dataTypeValueValidatePage.outputIsValid() }

        when:
        dataTypeValueValidatePage = browser.page DataTypeValueValidatePage
        dataTypeValueValidatePage.clearKeyField()
        dataTypeValueValidatePage.validateKeyField = "none"

        then:
        waitFor { !dataTypeValueValidatePage.outputIsValid() }

        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()
        then:
        at HomePage
    }
}

