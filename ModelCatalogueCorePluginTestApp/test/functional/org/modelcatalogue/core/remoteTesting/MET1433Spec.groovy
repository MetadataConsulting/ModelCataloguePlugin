package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.VersionsPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1433')
@Title('Verify navigation to old version of a model using the tree view')
@Narrative('''
- Login to the model Catalogue
- Click on any Data Model
- On the tree view, click on the Versions link
- Verify version is displaying at the end of every model
''')
@Ignore
class MET1433Spec extends GebSpec {

    void "Verify navigation to old version of a model using the tree view"() {
        when: 'login to the model catalogue'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when: 'select cancer model'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage

        when: 'On the tree view, click on the Versions link'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Versions')

        then:
        at VersionsPage

        when: 'Verify version is displaying at the end of every model'
        VersionsPage versionsPage = browser.page VersionsPage

        then:
        versionsPage.rowsContainText('0.0.1')
    }
}