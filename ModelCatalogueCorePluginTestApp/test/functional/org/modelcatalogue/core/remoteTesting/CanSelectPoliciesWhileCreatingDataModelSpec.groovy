package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.ApiKeyPage
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1604')
@Title('Data Model is created when selecting policies')
@Narrative('''
- Login to Model Catalogue
- Click on the create button
- Fill the form and select Select unique of kind or default checks
- Click on the green button
''')
@Ignore
class CanSelectPoliciesWhileCreatingDataModelSpec extends GebSpec {

    def "Data Model is created when selecting policies"() {
        when: "Login to Model Catalogue as Curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when: 'Fill the form and select Select unique of kind or default checks and Click on the green button'
        String uuid = UUID.randomUUID().toString()
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.check('Default Checks')
        createDataModelPage.submit()

        then:
        at DataModelPage
    }
}