package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AssetsPage
import org.modelcatalogue.core.geb.BusinessRulesPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import org.modelcatalogue.core.AssetWizardSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class QuickSearchSpec extends AbstractModelCatalogueGebSpec{

    private static final String quickSearch="#role_navigation-right_search-menu-menu-item-link > span.ng-scope.fa.fa-search"
    private static final String modelCatalogue = "span.mc-name"
    private static final String   search = "input#value"
    private static final String   createButton = "span.text-success"
    public static final String infiniteTableRow = '.inf-table tbody .inf-table-item-row'
    public static final String asset = 'asset'
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 2000

    @Ignore
    def "go to login"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Test 1')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Business Rules')

        then:
        at BusinessRulesPage

        and:
        check rightSideTitle contains  'Active Business Rules'
    }

    @Ignore
    def "create new validation rule"() {
        when:
        click createButton

        then:
        check modalDialog displayed

        when:
        fill 'name' with 'TESTING Validation Rule'

        click save

        then:
        check { infTableCell(1, 1) } contains 'TESTING Validation Rule'
    }

    @Ignore
    def "NAVIGATE BACK TO THE HOME PAGE"() {
        when:
        click modelCatalogue

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Test 2')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Assets')

        then:
        at AssetsPage

        and:
        check rightSideTitle is 'Active Assets'
    }

    @Ignore
    def "upload new asset"() {
        when:
        click createButton

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Sample testing'
        fill asset with file('example.xsd')

        click save

        then:
        check infiniteTableRow displayed
    }

    @Ignore
    def "navigate to quick search and search a data model"() {
        when:
        click modelCatalogue
        click quickSearch

        and:'select cancer'
        fill search with 'Cancer Model' and pick first item

        then:
        check rightSideTitle contains "Cancer Model"
    }

    @Ignore
    def "navigate to the quick search and search for a data class"() {

        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        fill search with "NHIC Datasets" and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        expect:
        check rightSideTitle contains "NHIC Datasets"
    }

    @Ignore
    def "navigate to quick search and search for a data element"() {
        when:
        click modelCatalogue
        click quickSearch

        and:
        fill search with "Test Element 1" and pick first item

        then:
        check rightSideTitle contains "Test Element 1"
    }

    @Ignore
    def "quick search a data type"() {

        when:
        click modelCatalogue
        click quickSearch

        and:
        fill search with "xs:string" and pick first item

        then:
        check rightSideTitle contains'xs:strin'
    }

    @Ignore
    def "quick search a Measurement unit"() {

        when:
        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "second" and pick first item

        then:
        check rightSideTitle contains "second"
    }

    @Ignore
    def "quick search a business rule"() {
        when:
        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "TESTING Validation Rule" and pick first item

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check rightSideTitle contains "TESTING Validation Rule"
    }

    @Ignore
    def "quick search an asset"() {
        when:
        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "Sample testing" and pick first item

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check rightSideTitle contains "testing"
    }

    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }
}
