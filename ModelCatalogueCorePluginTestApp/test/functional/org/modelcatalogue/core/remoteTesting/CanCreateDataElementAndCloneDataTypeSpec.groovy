package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title
import spock.lang.Ignore

@Issue('https://metadata.atlassian.net/browse/MET-1447')
@Title('Verify that curator can create a new Data Element and clone data type')
@Narrative('''
- Login to Model Catalogue as curator
- Create a data model 
- Create a Data Types
- Create a Data Element with the created Data Type
- Select the created data type
- On the top menu, click on the Data Element link
- Scroll down and click on Clone The Current Element Into Another Element
- Select the destination data model for the cloned element and click on OK button
- Verify that Data Element and Type are cloned
''')
class CanCreateDataElementAndCloneDataTypeSpec extends GebSpec {

    def "Verify that curator can create a new Data Element and clone data type"() {
        given:
        final String uuid = UUID.randomUUID().toString()
        final String dataTypeName = 'TypeMET1447'
        final String myName = " testing data element"
        final String myCatalogue = UUID.randomUUID().toString()
        final String myDescription = "This a test element"

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'click the create data model button'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.check('Default Checks')
        createDataModelPage.submit()

        then:
        at DataModelPage

        when: "Create a Data Types"
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.dataModelTitle.contains uuid

        when:
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page(DataTypesPage)
        dataTypesPage.createDataTypeFromNavigation()

        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeName
        createDataTypePage.description = "my description of data type -1"
        createDataTypePage.buttons.save()
        Thread.sleep(5000)

        then:
        at DataTypesPage

        when: "Create Data Element"
        Thread.sleep(5000)
        dataModelPage = browser.page DataModelPage
        Thread.sleep(5000)
        dataModelPage.treeView.select('Data Elements')

        then:
        at DataElementsPage

        when: "navigate to data element creation page"
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when: "fill the create data element form"
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = myName
        createDataElementPage.modelCatalogueId = myCatalogue
        createDataElementPage.description = myDescription
        createDataElementPage.search(dataTypeName)
        sleep(2_000)
        createDataElementPage.selectFirstItem()
        createDataElementPage.finish()

        then:
        at DataElementsPage

        when:
        dataElementsPage = browser.page(DataElementsPage)

        then:
        dataElementsPage.dataElementCreated().trim() == myName.trim()
    }
}