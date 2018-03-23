package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.Unroll

@Issue('https://metadata.atlassian.net/browse/MET-1566')
@Stepwise
class MET1566Spec extends AbstractModelCatalogueGebSpec {
    private static
    final String addItemIcon = "div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle"

    def "Login to Model Catalogue with curator and select Cancer Model"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage
    }

    @Unroll("Going to Test  Add #sno for #treeItem")
    def "Navigate and Select #treeItem"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select(treeItem)

        then:
        check Common.rightSideTitle contains "Active $treeItem"
        $(addItemIcon).displayed == false

        where:
        sno | treeItem
        1   | "Data Classes"
        2   | "Data Elements"
        3   | "Data Types"
        4   | "Measurement Units"
        5   | "Business Rules"
        6   | "Assets"
        7   | "Tags"

    }
}