package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.ImportedDataModelsPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1654')
@Title('Check that a viewer is not able to Import Data Models on models they have read only rights to')
@Narrative('''
- login to model catalogue as viewer
- Select data model that user has read-only rights to 
- Navigate to the tree view and select  Imported Data Models 
- Click on the grey plus button
''')
@Ignore
class UnableToImportIfReadAccessSpec extends GebSpec {

    def "Login to Model Catalouge"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: "Select data model that user has read-only rights to"
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Cancer Model')
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage

        when: "Navigate to the tree view and select Imported Data Models"
        DataModelPage dataModelPage = browser.page(DataModelPage)
        dataModelPage.treeView.select('Imported Data Models')

        then:
        at ImportedDataModelsPage

        when: "Click on the grey plus button"
        ImportedDataModelsPage importedDataModelsPage = browser.page(ImportedDataModelsPage)

        then:
        !importedDataModelsPage.areCreateButtonsVisible()
    }
}