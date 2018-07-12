package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-2026')
@Title('Check that the user is not capable to clone unauthorized Element into an authorized model')
@Narrative('''
- Login to Model Catalogue As Supervisor
- Select 'create new data model ' button ( black plus sign) in the right hand top menu to create a data model | Redirected to Create New Data Model page
- Populate Data Model Name Catalogue ID, Description and click save | Data model is created. Redirected to Data Model page
- In data model, navigate to data elements and click the green plus button to create new data elements | Data Element pop - up Wizard appears
- Populate data wizard form with Data Element name, catalogue ID and description . Press save | Data Element is created
- Repeat steps 2-5 | Second new data model containing a new element type is created
- Repeat steps 2-5 | Third new data model containing a new Element type is created
- Navigate and click on Settings menu button ( top right hand side) and from drop-down menu, select Data Model ACL | Redirected to Data Model ACL page - Data Model Permissions is title
- Select the first data model that you created . | Go to data model permissions page. Data Model name is the title .
- Select Curator from drop down list and from second drop down list give them administration rights over this data model | Curator appears in list of users with access to data model
- repeat steps 9-10 for the second data model created | The curator is given administration rights for the second data model
- Do nothing in regards to permissions for the third model. | By doing nothing, assurance is given that Curator is not authorised to view the third data model.
- Log out as supervisor | supervisor is logged out
- Log in as curator | Curator is logged in
- Select the first data model that you created as supervisor that the Curator is authorised to view | Taken to Data model 'homepage\'
- On the top left menu, click on the data model menu button | drop-down menu appears
- Scroll down and click on Clone Another Element Into Current Data Model | Pop up wizard appears with search for element to clone into data model
- Click on the 'element' icon to the left of the search bar to bring up list of elements | list of elements are shown
- Check to see if any element from Data Models that you are not authorised to see from the third data model are present. If they are, select one to be cloned and click on the OK button | Pop-up stating that you are not authorised to clone the element appears asking you to sign in as a different user.
- If no unauthorised data elements are present, try to search for them in the search bar. If they come up in the search, select one. | Pop -up stating that you are not authorise to cone the element appears, asking you to sign in as a different user.
- if a permissions pop-up appears, press cancel. Check that Element is not cloned
- Select a Data Element from the second data model created . Click Save. | Data Element populates form. Data Element is cloned into this model
- Check that Data Element from second data model now exists in first data model
''')
@Stepwise
class CloneUnauthorizedElementSpec extends GebSpec {

    @Shared
    String dataModelOneName = UUID.randomUUID().toString()
    @Shared
    String dataModelTwoName = UUID.randomUUID().toString()
    @Shared
    String dataModelThreeName = UUID.randomUUID().toString()
    @Shared
    String dataTypeOneName = "DATATYPE_ONE"
    @Shared
    String dataTypeTwoName = "DATATYPE_TWO"
    @Shared
    String dataTypeThreeName = "DATATYPE_THREE"

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "create first data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelOneName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create first data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeOneName
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "create second data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelTwoName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create second data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeTwoName
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "create third data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelThreeName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create third data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeThreeName
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "select data model ACL"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPermission()
        then:
        at DataModelAclPermissionsPage
    }

    def "grant admin right to curator for first data model"() {
        when:
        DataModelAclPermissionsPage dataModelAclPermissionsPage = browser.page DataModelAclPermissionsPage
        dataModelAclPermissionsPage.select(dataModelOneName)
        then:
        at DataModelAclPermissionsShowPage

        when:
        DataModelAclPermissionsShowPage dataModelAclPermissionsShowPage = browser.page DataModelAclPermissionsShowPage
        dataModelAclPermissionsShowPage.grant("curator", "administration")
        then:
        at DataModelAclPermissionsShowPage
    }

    def "grant admin right to curator for second data model"() {
        when:
        DataModelAclPermissionsPage dataModelAclPermissionsPage = to DataModelAclPermissionsPage
        dataModelAclPermissionsPage.select(dataModelTwoName)
        then:
        at DataModelAclPermissionsShowPage

        when:
        DataModelAclPermissionsShowPage dataModelAclPermissionsShowPage = browser.page DataModelAclPermissionsShowPage
        dataModelAclPermissionsShowPage.grant("curator", "administration")
        then:
        at DataModelAclPermissionsShowPage
    }

    def "logout as supervisor"() {
        when:
        DataModelAclPermissionsShowPage dataModelAclPermissionsShowPage = browser.page DataModelAclPermissionsShowPage
        dataModelAclPermissionsShowPage.nav.userMenu()
        dataModelAclPermissionsShowPage.nav.logout()
        then:
        at HomePage
    }

    def "login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "select first data model created"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        Thread.sleep(2000)
        dashboardPage.search(dataModelOneName)
        dashboardPage.select(dataModelOneName)

        then:
        at DataModelPage
    }

    def "clone another element into current data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.cloneAnotherElement()
        then:
        at CloneIntoDataModulePage
    }

    def "check data element from third model is not clonable"() {
        when:
        CloneIntoDataModulePage cloneIntoDataModulePage = browser.page CloneIntoDataModulePage
        cloneIntoDataModulePage.listAllDataModels()
        then:
        at SearchCatalogElementPage

        when:
        SearchCatalogElementPage searchCatalogElementPage = browser.page SearchCatalogElementPage
        searchCatalogElementPage.searchCatalogElement(dataTypeThreeName)
        then:
        at LoginModalPage

        when:
        LoginModalPage loginModalPage = browser.page LoginModalPage
        loginModalPage.cancel()
        then:
        at DashboardPage
    }

    def "check data element of first model is clonable"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search(dataModelOneName)
        dashboardPage.select(dataModelOneName)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        sleep(3_000)
        dataModelPage.dropdown()
        dataModelPage.cloneAnotherElement()
        then:
        at CloneIntoDataModulePage

        when:
        CloneIntoDataModulePage cloneIntoDataModulePage = browser.page CloneIntoDataModulePage
        cloneIntoDataModulePage.listAllDataModels()
        then:
        at SearchCatalogElementPage

        when:
        SearchCatalogElementPage searchCatalogElementPage = browser.page SearchCatalogElementPage
        searchCatalogElementPage.searchCatalogElement(dataTypeTwoName)

        then:
        waitFor(5) { at CloneIntoDataModulePage }

        when:
        CloneIntoDataModulePage cloneIntoDataModulePage1 = browser.page CloneIntoDataModulePage
        cloneIntoDataModulePage1.cloneModal()
        then:
        waitFor(5) { at DataTypePage }
    }

    def "check data element of second data model is cloned"() {
        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        then:
        dataTypePage.isDataTypePageFor(dataTypeTwoName) 
    }
}
