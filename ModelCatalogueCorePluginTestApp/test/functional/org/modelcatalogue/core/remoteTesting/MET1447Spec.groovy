package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.*
import spock.lang.Stepwise

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
@Stepwise
@Ignore
class MET1447Spec extends GebSpec {

    @Shared
    String uuid = UUID.randomUUID().toString()
    @Shared
    String dataTypeName = 'TypeMET1447'
    @Shared
    String myName = " testing data element"
    @Shared
    String myCatalogue = UUID.randomUUID().toString()
    @Shared
    String myDescription = "This a test element"
    @Shared
    String dataTypeCreated = 'tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'

    def "Login to Model Catalogue as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "Create a data model"() {
        when:
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
    }

    def "Create a Data Types"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.titleContains uuid

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
    }

    def "Create Data Element"() {
        when:
        Thread.sleep(5000)
        DataModelPage dataModelPage = browser.page DataModelPage
        Thread.sleep(5000)
        dataModelPage.treeView.select('Data Elements')

        then:
        at DataElementsPage
    }

    def "navigate to data element creation page"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage
    }

    def "fill the create data element form"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = myName
        createDataElementPage.modelCatalogueId = myCatalogue
        createDataElementPage.description = myDescription
        createDataElementPage.search(dataTypeName)
        $("a.cep-item", text: dataTypeName).text()
        $("a.cep-item", text: dataTypeName).size()
        createDataElementPage.selectFirstItem(dataTypeName)
        createDataElementPage.finish()
        Thread.sleep(5000)

        then:
        at DataElementsPage
        $(dataTypeCreated).text()?.trim() == myName.trim()
    }
}