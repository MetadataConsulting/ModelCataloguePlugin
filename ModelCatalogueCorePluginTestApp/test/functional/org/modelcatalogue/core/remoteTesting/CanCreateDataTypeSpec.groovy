package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataTypePage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1630')
@Title('Check that a viewer is not able to create a data type for unauthorised model')
@Narrative('''
- login to model catalogue
- Select a data model user is not authorised to administrate
- Navigate to the tree view and select Data Types
- Click on the grey plus button
''')
@Ignore
class CanCreateDataTypeSpec extends GebSpec {

    def "Check that a viewer is not able to create a data type for unauthorised model"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'select a data model for which the user has administration roles'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Test 1')
        dashboardPage.select('Test 1')

        then:
        at DataModelPage

        when: 'Within the tree, select Data Types'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage

        when: 'navigate to data type creation page'
        DataTypesPage dataTypesPage = browser.page DataTypesPage

        then:
        dataTypesPage.areCreateButtonsVisible()

        when:
        int numberOfDataTypes = dataTypesPage.count()
        dataTypesPage.createDataTypeFromNavigation()

        then:
        at CreateDataTypePage

        when: "fill the create data type form"
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = 'dataTypeName'
        createDataTypePage.description = "my description of data type"
        createDataTypePage.buttons.save()

        then: 'user is again in the data types page'
        at DataTypesPage

        when:
        dataTypesPage = browser.page DataTypesPage

        then: 'a new data type is in the data types list'
        waitFor { dataTypesPage.count() == ( numberOfDataTypes + 1 ) }
    }
}