package org.modelcatalogue.core.regression

import org.modelcatalogue.core.geb.*
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getRightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
@Ignore
class CustomMetadataNotCarriedNewVersionSpec extends AbstractModelCatalogueGebSpec {

    private static final String first_row ='tr.inf-table-item-row>td:nth-child(1)'
    private static final String version ='div.active>span:nth-child(2)>span'

    def "login to model catalogue and select version"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Clinical Tags')
        dashboardPage.select('Clinical Tags')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Versions')

        then:
        at VersionsPage

        and:
        check rightSideTitle contains 'Clinical Tags History'

        and:
        check first_row contains '0.0.1'
    }
}
