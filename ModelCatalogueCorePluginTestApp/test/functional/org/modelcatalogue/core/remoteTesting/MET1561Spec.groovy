package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise

@Stepwise
@Issue('https://metadata.atlassian.net/browse/MET-1561')
class MET1561Spec extends AbstractModelCatalogueGebSpec {
    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')

    def "login to MDX as curator, selected a finalized data model and click export"() {
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

        when:
        click exportAction

        then:
        noExceptionThrown()
    }
}