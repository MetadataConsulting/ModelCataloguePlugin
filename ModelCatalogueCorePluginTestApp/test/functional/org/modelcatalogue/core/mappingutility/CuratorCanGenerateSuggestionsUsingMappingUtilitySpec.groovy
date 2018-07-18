package org.modelcatalogue.core.mappingutility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1662')
@Title('Examine that user can generate Suggestion using Mapping Utility')
@Narrative('''
- Login to Model Catalogue as curator
- Create two Data Models, A and B, with random names. Create a data element in each of them named "Test Data Element".
- On the top menu, click on the Settings menu button from the top left hand menu | Drop-down menu appears
- Select Mapping Utility form the drop dwon menu | Mapping batches page is open
- Select button to the right of the page called 'Generate Mappings' | Generate Suggestions page is opened
- Populate form by selecting names of Data Models A and B from the dropdown menus. Select option for type of optimisation  and then click button called 'Generate'  | Taken back to Mapping batches page, popup states that mapping suggestions are being generated. 
- Refresh the page to see Mapping suggestions | Mapping suggestions appear
- Verify that suggestion are created once page is refreshed 
''')
@Stepwise
class CuratorCanGenerateSuggestionsUsingMappingUtilitySpec extends GebSpec {

    @Shared
    String modelOneOption
    @Shared
    String modelTwoOption
    @Shared
    String dataModelNameA = UUID.randomUUID().toString()
    @Shared
    String dataElementNameA = "Test Data Element A"
    @Shared
    String dataElementNameB = "Test Data Element B"
    @Shared
    String dataModelNameB = UUID.randomUUID().toString()

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "create data model A"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelNameA
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "go to data element page"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage
    }

    def "create data element for A"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataElementNameA
        createDataElementPage.finish()
        then:
        at DataElementsPage
    }

    def "create data model B"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelNameB
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "create data element for data model B"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataElementNameB
        createDataElementPage.finish()

        then:
        at DataElementsPage
    }


    def "go to mapping utility"() {

        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.mappingUtility()

        then:
        at MappingPage
    }

    def "generate mapping"() {
        when:
        MappingPage mappingPage = browser.page MappingPage
        mappingPage.generateMapping()
        then:
        at SuggestionsPage
    }

    def "create suggestions"() {
        when:
        SuggestionsPage suggestionsPage = browser.page SuggestionsPage
        modelOneOption = suggestionsPage.selectDataModelOne(dataModelNameA)
        modelTwoOption = suggestionsPage.selectDataModelTwo(dataModelNameB)
        suggestionsPage.generateSuggestion()
        then:
        at MappingPage
    }

    def "find generated mapping"() {
        when:
        MappingPage mappingPage = browser.page MappingPage
        driver.navigate().refresh()
        sleep(2_000)
        mappingPage.hasMapping(modelOneOption, dataModelNameA)
        then:
        at MappingSuggestionsPage
    }
}
