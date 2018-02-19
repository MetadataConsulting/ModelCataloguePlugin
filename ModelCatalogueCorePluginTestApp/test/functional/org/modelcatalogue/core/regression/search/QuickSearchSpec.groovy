package org.modelcatalogue.core.regression.search

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.admin
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import org.modelcatalogue.core.regression.asset.admin.AssetWizardSpec
import spock.lang.Ignore
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise


//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
@Stepwise
class QuickSearchSpec extends AbstractModelCatalogueGebSpec{

    private static final String quickSearch="#role_navigation-right_search-menu-menu-item-link > span.ng-scope.fa.fa-search"
    private static final String modelCatalogue = "span.mc-name"
    private static final String   search = "input#value"
    private static final String   createButton = "span.text-success"
    public static final String infiniteTableRow = '.inf-table tbody .inf-table-item-row'
    public static final String asset = 'asset'
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 2000


    def "go to login"() {
        login Common.admin

        when:
        select 'Test 1' select "Business Rules"

        then:
        check Common.rightSideTitle contains  'Active Validation Rules'
    }

    def "create new validation rule"() {
        when:
        click createButton

        then:
        check Common.modalDialog displayed

        when:
        fill 'name' with 'TESTING Validation Rule'

        click Common.save

        then:
        check { infTableCell(1, 1) } contains 'TESTING Validation Rule'
    }


    def "NAVIGATE BACK TO THE HOME PAGE"() {
        when:
        click modelCatalogue

        select "Test 2" select "Assets"

        then:
        check Common.rightSideTitle is 'Active Assets'
    }

    def "upload new asset"() {
        when:
        click createButton

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'Sample testing'
        fill asset with file('example.xsd')

        click Common.save

        then:
        check infiniteTableRow displayed
    }

    def "navigate to quick search and search a data model"() {
        when:
        click modelCatalogue
        click quickSearch

        and:'select cancer'
        fill search with 'Cancer Model' and Common.pick first Common.item

        then:
        check Common.rightSideTitle contains "Cancer Model"
    }
    def "navigate to the quick search and search for a data class"() {

        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        fill search with "NHIC Datasets" and Common.pick first Common.item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        expect:
        check Common.rightSideTitle contains "NHIC Datasets"


    }
    def "navigate to quick search and search for a data element"() {

        when:
        click modelCatalogue
        click quickSearch

        and:
        fill search with "Test Element 1" and Common.pick first Common.item

        then:
        check Common.rightSideTitle contains "Test Element 1"

    }
    def "quick search a data type"() {

        when:
        click modelCatalogue
        click quickSearch

        and:
        fill search with "xs:string" and Common.pick first Common.item

        then:
        check Common.rightSideTitle contains'xs:strin'
    }

    def "quick search a Measurement unit"() {

        when:
        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "second" and Common.pick first Common.item

        then:
        check Common.rightSideTitle contains "second"
    }

    def "quick search a business rule"() {

        when:
        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "TESTING Validation Rule" and Common.pick first Common.item

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check Common.rightSideTitle contains "TESTING Validation Rule"
    }

    def "quick search an asset"() {

        when:
        click modelCatalogue
        click quickSearch
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with "Sample testing" and Common.pick first Common.item

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check Common.rightSideTitle contains "testing"


    }
    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }


}
