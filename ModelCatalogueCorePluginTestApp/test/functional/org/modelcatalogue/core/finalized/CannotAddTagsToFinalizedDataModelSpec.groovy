package org.modelcatalogue.core.finalized

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.MeasurementUnitsPage
import org.modelcatalogue.core.geb.TagsPage
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class CannotAddTagsToFinalizedDataModelSpec extends GebSpec {
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

    def "Navigate to tree view and click on the Tags"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Tags')

        then:
        at TagsPage
    }

    def "Verify that you can not create a new Measurement Unit"() {
        when:
        TagsPage tagsPage = browser.page TagsPage

        then:
        !tagsPage.isAddItemIconVisible()
    }
}