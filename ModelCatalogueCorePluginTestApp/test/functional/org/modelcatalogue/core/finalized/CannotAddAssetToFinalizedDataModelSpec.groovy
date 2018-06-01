package org.modelcatalogue.core.finalized

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AssetsPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.MeasurementUnitsPage
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class CannotAddAssetToFinalizedDataModelSpec extends GebSpec {
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
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage
    }

    def "Navigate to tree view and click on the Assets"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Assets')

        then:
        at AssetsPage
    }

    def "Verify that you can not create a new Measurement Unit"() {
        when:
        AssetsPage assetsPage = browser.page AssetsPage

        then:
        !assetsPage.isAddItemIconVisible()
    }
}