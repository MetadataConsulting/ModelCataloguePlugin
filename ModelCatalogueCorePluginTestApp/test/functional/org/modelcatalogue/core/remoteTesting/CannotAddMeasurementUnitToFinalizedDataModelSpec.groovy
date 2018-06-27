package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class CannotAddMeasurementUnitToFinalizedDataModelSpec extends GebSpec {
    def "Login to Model Catalogue"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "Select a Finalized Model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Cancer Model')
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage
    }

    def "Navigate to tree view and click on the Measurement Units"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Measurement Units')

        then:
        at MeasurementUnitsPage
    }

    def "Verify that you can not create a new Measurement Unit"() {
        when:
        MeasurementUnitsPage measurementUnitsPage = browser.page MeasurementUnitsPage

        then:
        !measurementUnitsPage.isAddItemIconVisible()
    }
}