package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataClassPage
import org.modelcatalogue.core.geb.DataClassesPage
import org.modelcatalogue.core.geb.DataModelAclPermissionsPage
import org.modelcatalogue.core.geb.DataModelAclPermissionsShowPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1495')
@Title('Examine that Curator can edit data classes for Data Model they have administration rights to')
@Narrative('''
- Login to Model Catalogue as supervisor
- Select the 'create new data model' button ( black plus sign) from the top right hand menu. | Redirected to 'Create new data model' page
- Populate data model Name, Catalogue ID and description. Click save | Data Model is created. 
- Click on the Settings menu button in the top left hand menu  | Drop- down menu appears
- Select Data Model ACL from the drop-down menu | Redirected to Data Model ACL page . Data Model Permissions is the title 
- In the Data Model ACL (Access Clearance Level) page, Select the data model you just created from the list | Go to Data Model Users Permissions page  (title is name of data model) .  List  shown of users and permissions
- In Data Models Users Permissions page, From first drop down, select Curator's name ( Curator) and in the second drop down select Administration to give them administration rights
- Press the button 'Grant' in order to grand Curator administration rights to the data model  | Curator's name appears in list with Administration written in next column showing user rights. 
- Log out of Mx. | Supervisor is logged out
- Log in as Curator | Curator is Logged in
- Select a Draft Data Model
- on the tree view, select data Classes | Active Data Classes is displayed
- Select a data class | Taken to Data Class page
- Navigate to the right side and click on the form metadata link | Form Metadata section expands
- Click on the Edit button | Form Metadata Becomes a writable form with for boxes
- Fill the form and press the save button in the right hand corner ( looks like a tick )
- Check form metadata is edited
- Click on the Stewardship Metadata link if present
- Click on the edit button and fill the form
- Check that Stewardship Metadata is edited
- Click on the Metadata link if present
- Click on the edit button and fill the form
- Check that Metadata is edited     
''')
@Stepwise
class CuratorCanEditDataClassesForAdminDataModelSpec extends GebSpec {
    @Shared
    String dataModelName = UUID.randomUUID().toString()

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

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
        createDataModelPage.name = dataModelName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "select data model acl"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPermission()
        then:
        at DataModelAclPermissionsPage
    }

    def "grant admin right to curator for draft data model"() {
        when:
        DataModelAclPermissionsPage dataModelPermissionListPage = browser.page DataModelAclPermissionsPage
        dataModelPermissionListPage.select(dataModelName)
        then:
        at DataModelAclPermissionsShowPage

        when:
        DataModelAclPermissionsShowPage dataModelPermissionGrantPage = browser.page DataModelAclPermissionsShowPage
        dataModelPermissionGrantPage.grant("curator", "administration")

        then:
        at DataModelAclPermissionsShowPage
    }

    def "logout as supervisor"() {
        when:
        DataModelAclPermissionsShowPage dataModelPermissionGrantPage = browser.page DataModelAclPermissionsShowPage
        dataModelPermissionGrantPage.nav.userMenu()
        dataModelPermissionGrantPage.nav.logout()
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

    def "select a draft model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search(dataModelName)
        dashboardPage.select(dataModelName)
        then:
        at DataModelPage
    }

    def "open a data class"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Classes')
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page(DataClassesPage)
        dataClassesPage.openDataClass(0)

        then:
        at DataClassPage
    }

    def "edit form metadata"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.formMetadata()
        then:
        at DataClassPage
    }
}
