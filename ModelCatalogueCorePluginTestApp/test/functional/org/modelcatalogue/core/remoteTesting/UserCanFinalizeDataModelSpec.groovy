package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1634')
@Title('Check that user is able to finalize a data model')
@Narrative('''
- Login to model catalogue
- Create a data model
- Navigate to the top menu and select Data Model
- Scroll down and select Finalized
- Check that data model is finalized
''')
@Ignore
class UserCanFinalizeDataModelSpec extends GebSpec {

    def "Check that user is able to finalized a data model"() {
        given:
        final String uuid = UUID.randomUUID().toString()

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'click the create data model button'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then: 'the create data model form is opened'
        at CreateDataModelPage

        when: 'enter a random name for the data model and click create'
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.submit()

        then: 'data model page for the new data model is displayed'
        at DataModelPage

        when: 'Navigate to the top menu and select Data Model, Scroll down and select Finalized'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdown.finalize()

        then: 'The Finalize Data Model Pops up'
        at FinalizeDataModelPage

        when: 'Fill the finalized form and submit'
        FinalizeDataModelPage finalizeDataModelPage = to FinalizeDataModelPage
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()

        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage

        then: 'Check that data model is finalized'
        dataModelPage.titleContains("$uuid (0.0.1) finalized")
    }
}