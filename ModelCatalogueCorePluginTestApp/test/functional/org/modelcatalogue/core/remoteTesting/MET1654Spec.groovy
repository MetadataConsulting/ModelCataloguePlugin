package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.ImportedDataModelsPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Stepwise

@Issue('https://metadata.atlassian.net/browse/MET-1654')
@Stepwise
class MET1654Spec extends GebSpec {

    def "Login to Model Catalouge"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "Select data model that user has read-only rights to"() {
        when:
        DashboardPage dashboardPage = browser.page(DashboardPage)
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage
    }


    def "Navigate to the tree view and select Imported Data Models"() {
        when:
        DataModelPage dataModelPage = browser.page(DataModelPage)
        dataModelPage.treeView.select('Imported Data Models')

        then:
        at ImportedDataModelsPage
    }

    def "Click on the grey plus button"() {
        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page(ImportedDataModelsPage)

        then:
        !importedDataModelsPage.areCreateButtonsVisible()
    }
}