package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.ApiKeyPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Unroll

class ApiKeySpec extends GebSpec {

    @Unroll
    def "#username is able to change its apiKey"(String username, String password) {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login(username, password)

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.apiKey()

        then:
        at ApiKeyPage

        when:
        ApiKeyPage apiKeyPage = browser.page ApiKeyPage
        String oldApiKey = apiKeyPage.apiKey

        then:
        oldApiKey

        when:
        apiKeyPage.regenerate()

        then:
        apiKeyPage.apiKey != oldApiKey

        where:
        username     | password
        'supervisor' | 'supervisor'
        'curator'    | 'curator'
        'viewer'     | 'viewer'
    }
}
