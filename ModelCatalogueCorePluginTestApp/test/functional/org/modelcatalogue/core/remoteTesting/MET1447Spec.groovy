package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.CreateDataTypePage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
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
class MET1447Spec extends GebSpec {

    @Shared
    String uuid = UUID.randomUUID().toString()

    @Shared
    String dataTypeName = 'TypeMET1447'

    private static final myModel = "#my-models"
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String policies = "input#dataModelPolicy"
    private static final String finishButton = "#step-finish"
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 5000L

    private static final String modelHeaderName = 'h3.ce-name'
    private static final String first_row = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"



    private static final String search = "input#dataType"
    private static final String saveElement = "a#role_modal_modal-save-elementBtn"
    static String myName = " testing data element"
    static String myCatalogue = UUID.randomUUID().toString()
    static String myDescription = "This a test element"
    private static final String dataTypeCreated = 'tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'

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
        createDataTypePage.description = "my description of data type"
        createDataTypePage.buttons.save()

        then:
        check first_row contains dataTypeName
    }

    def "Create Data Element"() {
        when:
        selectInTree 'Data Elements'

        then:
        check Common.rightSideTitle is 'Active Data Elements'
    }

    def "navigate to data element creation page"() {
        when:
        click Common.create
        then:
        check Common.modalHeader contains 'Create Data Element'
    }

    def "fill the create data element form"() {
        when:
        fill Common.nameLabel with myName
        fill Common.modelCatalogueId with myCatalogue
        fill Common.description with myDescription

        and: 'select a data type'
        fill search with dataTypeName and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check saveElement displayed

        and: 'click on the save button'
        click saveElement
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        selectInTree 'Data Elements'

        then: 'verify that data is created'
        $(dataTypeCreated).text()?.trim() == myName.trim()
    }
}