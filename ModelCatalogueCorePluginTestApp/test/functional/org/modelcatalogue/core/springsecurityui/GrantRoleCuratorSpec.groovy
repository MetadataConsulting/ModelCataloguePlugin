package org.modelcatalogue.core.springsecurityui

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.UserEditPage
import org.modelcatalogue.core.geb.UserSearchPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1727')
@Title('Logged in as supervisor, able to change other users role from viewer to curator via backend')
@Narrative('''
- Login to MDX as supervisor/supervisor
- Click on settings menu button from top | Drop-down menu appears
- Select Users option from drop down menu | Taken to the backend page with title 'Spring Security Management Console' with a User Search box present.
- In the form box titled 'Username' Type the name of user ( that has a User role) and press the search button. | A list appears under the User Search box with results .
- Select user X's name from the list of results | Taken to page with 'Edit User ' title. User details are shown.
- Select the tab titled 'Roles' next to 'User Details | User Roles are shown.
- Tick the option next to Role_ Metadata_Curator | Check that both role_user and role_metadata_curator are now selected
- Select the button 'Update' to update user roles | User roles are updated
- Logout and log back in as User X
- Check that User X now sees the import menu next to the admin menu in the top left corner. The admin menu should also include the option for Mapping Utility
''')
@Stepwise
class GrantRoleCuratorSpec extends GebSpec {

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "open settings dropdown"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.users()
        then:
        at UserSearchPage
    }

    def "search user"() {
        when:
        UserSearchPage userSearchPage = browser.page UserSearchPage
        userSearchPage.fillUser("user")
        Thread.sleep(5000)
        userSearchPage.search()
        then:
        at UserSearchPage
    }

    def "select user"() {
        when:
        UserSearchPage userSearchPage = browser.page UserSearchPage
        userSearchPage.selectUser()

        then:
        at UserEditPage

    }

    def "select user role"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.clickRoles()
        then:
        at UserEditPage
    }

    def "assign user roles"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.assignMetadataCuratorRole()
        userEditPage.update()
        then:
        at UserEditPage
    }

    def "logout"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.logout()
        then:
        at HomePage
    }

    def "Login as user"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage
    }

    def "check import menu"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        dashboardPage.nav.importMenuLink.displayed
    }
}
