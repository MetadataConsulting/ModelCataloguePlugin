package org.modelcatalogue.core.july18

import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1480')
@Title('Verify that connection lost message is not displayed constantly')
@Stepwise
class VerifyConnectionLostMessageNotDisplayedConstantlySpec extends AbstractModelCatalogueGebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "Select data model and check connection lost message"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("Test 1")
        dashboardPage.select("Test 1")
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage=browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
        then:
        !checkIfConnectionLost()

        when:
        DataClassesPage dataClassesPage=browser.page DataClassesPage
        dataClassesPage.treeView.dataElements()
        then:
        !checkIfConnectionLost()

        when:
        DataElementsPage dataElementsPage=browser.page DataElementsPage
        dataElementsPage.treeView.dataTypes()
        then:
        !checkIfConnectionLost()

    }
}