package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import org.modelcatalogue.core.geb.*
import spock.lang.Stepwise

@Issue('https://metadata.atlassian.net/browse/MET-1959')
@Title('Verify that the viewer does not have access to Fast Actions(Imports)')
@Narrative('''
 - Login to Metadata Exchange As Supervisor | Login successful
 - Select Settings menu button from right-hand top menu | Settings menu Drop-down appears.
 - Select Users from the Settings drop-down menu | Redirected to the backend page with title 'Spring Security Management Console' with a User Search box present.
 - In the form box titled 'Username' type the name of User X (that has a User role) and press the search button | A list appears under the User Search box with results
 - Select User X's name from the list of results | Taken to page with 'Edit User' title. User details are shown
 - Select the tab titled 'Roles' next to 'User Details' | User Roles are shown
 - Verify that User X only has ROLE_USER ticked | Verified that User X has a role of user and nothing more
 - Log out of Metadata Exchange | Log out successful
 - Login to Metadata Exchange As User X | Login successful
 - On the top right hand-menu, Select the Settings menu Button | Settings menu drop-down appears
 - Verify that the only options on the settings menu drop down are: Code Version, Relationship Types, Data Model Policies and Feedbacks | Options for Settings menu drop down are limited to: Code Version, Relationship Types, Data Model Policies and Feedbacks
 - Logout of the Metadata Exchange | Logout successful
''')
@Stepwise
class VerifyViewerCannotAccessFactActionsSpec extends GebSpec {

    def "login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("supervisor", "supervisor")
        then:
        at DashboardPage
    }

    def "go to users section"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        then:
        at DashboardPage

        when:
        dashboardPage = browser.page DashboardPage
        dashboardPage.nav.users()
        then:
        at UserSearchPage
    }

    def "search user with user role"() {
        when:
        UserSearchPage userSearchPage = browser.page UserSearchPage
        userSearchPage.fillUser("user")
        userSearchPage.search()
        then:
        at UserSearchPage

        when:
        userSearchPage = browser.page UserSearchPage
        userSearchPage.selectUser("user")
        then:
        at UserEditPage
    }

    def "go to roles section"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.clickRoles()
        then:
        at UserEditPage
    }

    def "verify user has only user-role clicked"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        then:
        userEditPage.userRoleGranted()
        !userEditPage.metadatacuratorRoleGranted()
        !userEditPage.supervisorRoleGranted()
    }

    def "logout as supervisor"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.logout()
        then:
        at HomePage
    }

    def "login as user"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("user", "user")
        then:
        at DashboardPage
    }

    def "only limited options are coming in user dropdown"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        then:
        at DashboardPage

        when:
        dashboardPage = browser.page DashboardPage
        then:
        assert dashboardPage.nav.userdropdownLength() == 4
        dashboardPage.nav.codeVersionIsVisible()
        dashboardPage.nav.relationshiptypeIsVisible()
        dashboardPage.nav.datamodelpolicyIsVisible()
        dashboardPage.nav.feedbacksIsVisible()
    }
}
