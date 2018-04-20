package org.modelcatalogue.core.datatype

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataElementPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataElementsPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-2014')
@Title('Check that user is not able to create Data Element with unauthorized Data Type')
@Narrative('''
- Login to Model Catalogue As Supervisor
- Select 'create new data model ' button ( black plus sign) in the right hand top menu to create a data model | Redirected to Create New Data Model page
- Populate Data Model Name Catalogue ID, Description and click save | Data model is created. Redirected to Data Model page
- In data model, navigate to data types and click the green plus button to create new data type | Data Type pop - up Wizard appears
- Populate data wizard form with Data Type name, catalogue ID and description . Press save | Data type is created
- Repeat steps 2-5 | Second new data model containing a new data type is created
- Repeat steps 2-5 | Third new data model containing a new data type is created
- Navigate and click on Settings menu button ( top right hand side) and from drop-down menu, select Data Model ACL | Redirected to Data Model ACL page - Data Model Permissions is title
- Select the first data model that you created . | Go to data model permissions page. Data Model name is the title .
- Select Curator from drop down list and from second drop down list give them administration rights over this data model | Curator appears in list of users with access to data model
- repeat steps 9-10 for the second data model created | The curator is given administration rights for the second data model
- Do nothing in regards to permissions for the third model. | By doing nothing, assurance is given that Curator is not authorised to view the third data model.
- Log out as supervisor | supervisor is logged out
- Log in as curator | Curator is logged in
- Select first data model created from list | Directed to Data Model page
- On the tree view, select Data Elements to go to Data Elements page. | check that right side title is Active Data Elements. Page displays list of Data Elements within Data Model.
- Click on the green button at the bottom of the list | Pop up appears. Check that Model Header is Create Data Element
- Fill the name, catalogue ID and Description for the Data Element 
- At the bottom of the form, select the button on the left hand side of the box titled 'Data Type', to add a data type via import. | Pop up with search ability to search for Data type appears
- click on show All underneath the search bar to display all possible Data Types to import and check that only Data types from the first and second data mode that the curator is authorised to view appear. ( and any other data models the curator is authorised to view) | Only data models that the curator has been granted permission to view are in the list . The Data types from the third model are NOT shown
- Type in the name of the data type from second data model to see if it appears in search bar. If appears, a pop-up demanding password / stating you aren't authorised to use data model should appear. | Pop up demands you log in as a user that has been authorised to use Data Model. Click cancel and return to Data type search pop up .
- Confirm that user can only select data types that they are authorised to view .
- Select a data type from the second data model that curator is authorised to view | data type is is added into the form.
- Press save | Data type is saved
-  Confirm that new data type has been added to the data model | Data type appears in list under data types. tag
''')
@Stepwise
class CannotCreateDataElementWithUnauthorizedDataTypeSpec extends GebSpec {

    @Shared
    String dataModelOneName = "DATA_MODEL_ONE"
    @Shared
    String dataModelTwoName = "DATA_MODEL_TWO"
    @Shared
    String dataModelThreeName = "DATA_MODEL_THREE"
    @Shared
    String dataTypeOneName = "DATATYPE_ONE"
    @Shared
    String dataTypeTwoName = "DATATYPE_TWO"
    @Shared
    String dataTypeThreeName = "DATATYPE_THREE"
    @Shared
    String dataElementName = "DATA_ELEMENT"

    /*def "Login as supervisor"() {
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
        at DataModelPermissionListPage
    }

    def "grant admin right to curator for first data model"() {
        when:
        DataModelPermissionListPage dataModelPermissionListPage = browser.page DataModelPermissionListPage
        dataModelPermissionListPage.selectDataModal(dataModelOneName)
        then:
        at DataModelPermissionGrantPage

        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.selectUsername("curator")
        dataModelPermissionGrantPage.selectPermission("administration")
        dataModelPermissionGrantPage.grantPermission()
        then:
        at DataModelPermissionGrantPage
    }

    def "grant admin right to curator for second data model"() {
        when:
        DataModelPermissionListPage dataModelPermissionListPage = to DataModelPermissionListPage
        dataModelPermissionListPage.selectDataModal(dataModelTwoName)
        then:
        at DataModelPermissionGrantPage

        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.selectUsername("curator")
        dataModelPermissionGrantPage.selectPermission("administration")
        dataModelPermissionGrantPage.grantPermission()
        then:
        at DataModelPermissionGrantPage
    }

    def "logout as supervisor"() {
        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.nav.userMenu()
        dataModelPermissionGrantPage.nav.logout()
        then:
        at HomePage
    }*/

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
        dashboardPage.select(dataModelOneName)
        then:
        at DataModelPage
    }

    def "go to data element page"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage
    }

    def "create data element"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataElementName
        createDataElementPage.searchMore()
        createDataElementPage.showAllDataType()
        createDataElementPage.selectDataType(dataTypeThreeName)
        then:
        CreateDataElementPage
    }

    def "verify data element from first model is clonable"() {}
}
