package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
class MET1662Spec extends GebSpec {

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