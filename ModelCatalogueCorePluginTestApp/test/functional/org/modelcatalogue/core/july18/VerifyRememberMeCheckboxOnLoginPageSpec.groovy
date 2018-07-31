package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1431')
@Title('Test to verify (remember me) checkbox on the LoginPage​')
@Stepwise
class VerifyRememberMeCheckboxOnLoginPageSpec extends GebSpec {

    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.fillUsername("supervisor")
        loginPage.fillPassword("supervisor")
        loginPage.clickRememberMeCheckBox()
        submitButton.click()
        then:
        at DashboardPage
    }

    def "Shutdown and restart server"() {

    }

    def "Refresh page and check for login"() {
        when:
        browser.driver.navigate().refresh()
        then:
        at DashboardPage
    }

}