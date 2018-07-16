package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1563')
@Title('Verify that regular user can see api key')
@Narrative('''
 - Login to Metadata Exchange as supervisor | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
 - Logout of the Metadata Exchange | Logout successful
 - Login to Metadata Exchange as curator | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
 - Log out of the Metadata Exchange | Logout successful
 - Login to Metadata Exchange as viewer | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
 - Logout of Metadata Exchange | Logout successful.
 - Login to Metadata Exchange as user | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
''')
@Stepwise
class VerifyRegularUserCanSeeApiKeySpec extends GebSpec {

    def "login as supervisor"() {
        when:
        go("/login/auth")
        LoginPage loginPage = to LoginPage
        loginPage.login("supervisor", "supervisor")
        then:
        at DashboardPage
    }

    def "select api key option from user profile dropdown for supervisor"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.apiKey()
        then:
        at ApiKeyPage
    }

    def "regenerate api key for supervisor"() {
        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        String originalKey = apiKeyPage.getApiKey()
        apiKeyPage.regenerate()
        String newKey = apiKeyPage.getApiKey()
        then:
        assert originalKey != newKey
    }

    def "logout as supervisor"() {
        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        apiKeyPage.nav.userMenu()
        apiKeyPage.nav.logout()
        then:
        at HomePage
    }

    def "login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
        then:
        at DashboardPage
    }

    def "select api key option from user profile dropdown for curator"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.apiKey()
        then:
        at ApiKeyPage
    }

    def "regenerate api key for curator"() {
        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        String originalKey = apiKeyPage.getApiKey()
        apiKeyPage.regenerate()
        String newKey = apiKeyPage.getApiKey()
        then:
        assert originalKey != newKey
    }

    def "logout as curtor"() {
        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        apiKeyPage.nav.userMenu()
        apiKeyPage.nav.logout()
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

    def "select api key option from user profile dropdown for user"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.apiKey()
        then:
        at ApiKeyPage
    }

    def "regenerate api key for user"() {
        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        String originalKey = apiKeyPage.getApiKey()
        apiKeyPage.regenerate()
        String newKey = apiKeyPage.getApiKey()
        then:
        assert originalKey != newKey
    }

    def "logout as user"() {
        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        apiKeyPage.nav.userMenu()
        apiKeyPage.nav.logout()
        then:
        at HomePage
    }
}
