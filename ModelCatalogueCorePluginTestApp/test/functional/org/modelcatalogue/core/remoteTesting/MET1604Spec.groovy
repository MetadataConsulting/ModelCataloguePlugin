package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

class MET1604Spec extends AbstractModelCatalogueGebSpec {

    @Issue('https://metadata.atlassian.net/browse/MET-1604')
    def "Data Model is created when selecting policies"() {
        given:
        String uuid = UUID.randomUUID().toString()

        when: "Login to Model Catalogue as Curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage

        when: 'Click on the create button'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then: 'Create Data Model form is displayed'
        at CreateDataModelPage

        when: 'Fill the form and select Unique of a Kind or default checks'
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.check('Default Checks')
        createDataModelPage.submit()

        then:
        at DataModelPage
    }
}