package org.modelcatalogue.core.mappingutility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.MappingPage
import org.modelcatalogue.core.geb.MappingSuggestionsPage
import org.modelcatalogue.core.geb.SuggestionsPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title
import spock.lang.Ignore

@Issue('https://metadata.atlassian.net/browse/MET-1662')
@Title('Examine that user can generate Suggestion using Mapping Utility')
@Narrative('''
- Login to Model Catalogue as curator
- On the top menu, click on the Settings menu button from the top left hand menu | Drop-down menu appears
- Select Mapping Utility form the drop dwon menu | Mapping batches page is open
- Select button to the right of the page called 'Generate Mappings' | Generate Suggestsion page is opened
- Populate form buy selecting name of Data Models from list from Data Mdoel 1 and Data Model 2 drop down. Select option for type of optimization and click button called "Generate" | Taken back to Mapping batches page, popup states that mapping suggestions are being generated. 
- Refresh the page to see Mapping suggestions | Mapping suggestions appear
- Verify that suggestion are created once page is refreshed 
''')
@Stepwise
class CuratorCanGenerateSuggestionsUsingMappingUtilitySpec extends GebSpec {

    @Shared
    String modelOneOption
    @Shared
    String modelTwoOption

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "go to mapping utility"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
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
        modelOneOption = suggestionsPage.selectDataModelOne().replaceAll(~/\d.\d.\d/, "")
        modelTwoOption = suggestionsPage.selectDataModelTwo().replaceAll(~/\d.\d.\d/, "")
        println(modelOneOption)
        suggestionsPage.generateSuggestion()
        then:
        at MappingPage
    }

    def "find generated mapping"() {
        when:
        MappingPage mappingPage = browser.page MappingPage
        driver.navigate().refresh()
        mappingPage.hasMapping(modelOneOption, modelTwoOption)
        then:
        at MappingSuggestionsPage
    }
}
