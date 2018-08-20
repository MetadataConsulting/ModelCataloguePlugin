package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1439')
@Title('Verify that user can clone an Element')
@Stepwise
class VerifyThatUserCanCloneElementSpec extends GebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()

    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "select datamodel page"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("Cancer Models")
        dashboardPage.select("Cancer Models")
        then:
        at DataModelPage
    }
}