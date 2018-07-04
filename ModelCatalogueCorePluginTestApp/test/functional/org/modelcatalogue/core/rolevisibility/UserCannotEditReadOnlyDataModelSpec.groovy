package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelAclPermissionsPage
import org.modelcatalogue.core.geb.DataModelAclPermissionsShowPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title
import spock.lang.Ignore

@Issue('https://metadata.atlassian.net/browse/MET-1512')
@Title('Check that User should not be able to edit when logged as a viewer with read only rights for current Data Model')
@Narrative('''
- Login to model Catalogue ( as supervisor )
- Create a Data Model 
- Navigate to the Settings Menu button in the top left hand menu.
- Select Data Model ACL  from drop-down menu 
- Select newly created Data model from list 
- Grant user "user" read only access to the created data model by selecting their name from the first drop down and their permission level in the second drop down. Press 'Grant' button to update permission levels.
- Logout as supervisor  
- Login as user ( as user ) 
- Select Data Model that User has been granted read-only rights for  | Take to Data Model Page
- Check the inline edit button ( top right corner of data model homepage)  for the data model is disabled | User cannot edit the data model
''')
@Stepwise
class UserCannotEditReadOnlyDataModelSpec extends GebSpec {

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

    def "grant user read only access to created data model"() {
        when:
        DataModelAclPermissionsPage dataModelPermissionListPage = browser.page DataModelAclPermissionsPage
        dataModelPermissionListPage.select(dataModelName)
        then:
        at DataModelAclPermissionsShowPage

        when:
        DataModelAclPermissionsShowPage dataModelPermissionGrantPage = browser.page DataModelAclPermissionsShowPage
        dataModelPermissionGrantPage.grant('user', 'read')

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

    def "login as user"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage
    }

    def "select data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search(dataModelName)
        dashboardPage.select(dataModelName)
        then:
        at DataModelPage
    }

    def "check inline edit button is disabled"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        !dataModelPage.inlineEditButtonPresent()
    }
}