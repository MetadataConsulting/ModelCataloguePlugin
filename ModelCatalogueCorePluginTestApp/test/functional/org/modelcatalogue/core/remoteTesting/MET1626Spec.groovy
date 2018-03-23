package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import spock.lang.Issue
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@Issue('https://metadata.atlassian.net/browse/MET-1626')
class MET1626Spec extends AbstractModelCatalogueGebSpec {
    private static final String search = "input#elements"
    private static
    final String table = "#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    private static final String modelHeader = "div.modal-header>h4"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

    @Shared
    String dataModelName = 'Test 1'

    def "Login to Model catalogue"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "Select a draft Data Model"() {
        when: "Selected an draft Data Model"
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)

        then: "Data Model Page Should Open"
        at DataModelPage
    }

    def "navigate to the top menu and select create relationship"() {
        when: 'navigate to createRelationship page'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdown.addImport()

        then: 'verify that the text Destination is displayed'
        check modelHeader displayed
    }

    @Ignore
    def "select a data model"() {
        when: 'select a model'
        fill search with "Clinical Tags " and Common.pick first Common.item
        click Common.modalPrimaryButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)

        then: 'verify that  imports is displayed inside table'
        check table contains "imports"
    }
}