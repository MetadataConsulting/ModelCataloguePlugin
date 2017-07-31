package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.AssetWizardSpec

import static org.modelcatalogue.core.geb.Common.admin
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@Stepwise
class QuickSearchSpec extends AbstractModelCatalogueGebSpec{

    private static final String quickSearch="#role_navigation-right_search-menu-menu-item-link > span.ng-scope.fa.fa-search"
    private static final String  create  = "a#role_data-models_create-data-modelBtn"
    private static final String modelCatalogue = "span.mc-name"
    private static final String   search = "input#value"
    private static final String   createButton = "span.text-success"
    public static final String infiniteTableRow = '.inf-table tbody .inf-table-item-row'
    public static final String asset = 'asset'
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000


    def "go to login"() {
        login admin

        when:
        select 'Test 1' select "Business Rules"

        then:
        check rightSideTitle contains  'Active Validation Rules'
    }

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
    def "NAVIGATE BACK TO THE HOME PAGE"() {
        when:
        click modelCatalogue

        select "Test 2" select "Assets"

        then:
        check rightSideTitle is 'Active Assets'
    }

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
    def "navigate to quick search and search a data model"(){

        when:
        click modelCatalogue
        click quickSearch

        and:'select cancer'
        fill search with 'Cancer Model' and pick first item

        then:
        check rightSideTitle contains "Cancer Model"
    }
    def"navigate to the quick search and search for a data class"(){

        driver.navigate().back()
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        fill search with "NHIC Datasets" and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        expect:
        check rightSideTitle contains "NHIC Datasets"


    }
    def" navigate to quick search and search for a data element"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with " MET-523.M1.DE1" and pick first item

        then:
        check rightSideTitle contains "MET-523.M1.DE1"

    }
    def" quick search a data type"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with "MET-523.M1.VD1" and pick first item

        then:
        check rightSideTitle contains'MET-523.M1.VD1'

    }
    def"quick search a Measurement unit"(){

        when:
        driver.navigate().back()
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "MET-523.MU1" and pick first item

        then:
        check rightSideTitle contains "MET-523.MU1"

    }

    def"quick search a business rule"(){

        when:
        driver.navigate().back()
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "TESTING Validation Rule" and pick first item

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check rightSideTitle contains "TESTING Validation Rule"

    }

    def"quick search an asset"(){

        when:
        driver.navigate().back()
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
