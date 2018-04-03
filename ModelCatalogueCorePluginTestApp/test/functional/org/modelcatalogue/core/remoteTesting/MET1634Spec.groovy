package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.FinalizeDataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1634')
@Title('Check that user is able to finalized a data model')
@Narrative('''
- Login to model catalogue
- Create a data model
- Navigate to the top menu and select Data Model
- Scroll down and select Finalized
- Check that data model is finalized
''')
@Stepwise
class MET1634Spec extends GebSpec {
    @Shared
    String uuid = UUID.randomUUID().toString()

    def "Login to model catalogue"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "Create a data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "Navigate to the top menu and select Data Model, Scroll down and select Finalized"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdown.finalize()

        then:
        at FinalizeDataModelPage

        when:
        FinalizeDataModelPage finalizeDataModelPage = to FinalizeDataModelPage
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "Check that data model is finalized"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.titleContains("$uuid (0.0.1) finalized")
    }
}