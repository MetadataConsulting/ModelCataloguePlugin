package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.VersionsPage
import spock.lang.Issue

class MET1433Spec extends GebSpec {

    @Issue('https://metadata.atlassian.net/browse/MET-1433')
    void "Verify navigation to old version of a model using the tree view"() {
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
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Versions')

        then:
        at VersionsPage

        when:
        VersionsPage versionsPage = browser.page VersionsPage

        then:
        versionsPage.rowsContainText('0.0.1')
    }
}