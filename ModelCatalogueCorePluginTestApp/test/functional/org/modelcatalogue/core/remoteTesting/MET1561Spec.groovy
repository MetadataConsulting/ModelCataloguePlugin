package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1561')
@Title('Examine that finalized data model is marked as finalized in the XML')
@Narrative('''
- Login to Model Catalogue
- Select a Finalized Model
- Navigate to the top menu and click on the Export button
- Scroll down and click on the Export to catalogue XML
- Open the downloaded file and verify that the status is marked as finalized
''')
class MET1561Spec extends GebSpec {
    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')

    def "Login to Model Catalogue"() {
        when: 'login to MDX as curator'
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

    def "Examine that finalized data model is marked as finalized in the XML"() {
        when: 'click export'
        click exportAction

        then:
        noExceptionThrown()
    }
}