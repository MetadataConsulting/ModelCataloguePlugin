package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1438')
@Title('Verify Rare Disease Website Generator')
@Stepwise
class VerifyRareDiseaseWebsiteGeneratorSpec extends GebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()


    def "login as supervisor"() {

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')
        then:
        at DashboardPage
    }

    def "select datamodel page"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("Test 1")
        dashboardPage.select("Test 1")
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdownMenu.createDataType()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeName
        createDataTypePage.buttons.save()
        then:
        at DraftDataModelListPage
    }
}