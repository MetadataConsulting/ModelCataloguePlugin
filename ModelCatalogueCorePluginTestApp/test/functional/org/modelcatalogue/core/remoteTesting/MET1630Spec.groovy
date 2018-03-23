package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.CreateDataTypePage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Narrative
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1630')
@Title('Check that a viewer is not able to create a data type for unauthorised model')
@Narrative('''
- login to model catalogue
- Select a data model user is not authorised to administrate
- Navigate to the tree view and select Data Types
- Click on the grey plus button
''')
@Stepwise
class MET1630Spec extends AbstractModelCatalogueGebSpec {

    def "Login to Model Catalogue"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "select a data model for which the user has administration roles"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Test 1')

        then:
        at DataModelPage
    }

    def "Within the tree, select Data Types"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage
    }

    def "navigate to data type creation page "() {
        when:
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

        when: 'the user clicks the green plus button in the button'
        numberOfDataTypes = dataTypesPage.count()
        dataTypesPage.createDataTypeFromTableFooter()

        then: 'the create data type modal opens'
        at CreateDataTypePage

        when: "fill the create data type form"
        createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = 'dataTypeName 2'
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