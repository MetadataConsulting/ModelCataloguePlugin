package org.modelcatalogue.core.sanityTestSuite.LandingPage

import static org.modelcatalogue.core.geb.Common.admin
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@Stepwise
class QuickSearchSpec extends AbstractModelCatalogueGebSpec{

    private static final String quickSearch="#role_navigation-right_search-menu-menu-item-link > span.ng-scope.fa.fa-search"
    private static final String  create  = "a#role_data-models_create-data-modelBtn"
    private static final String   search = "input#value"


    def " login to model catalogue"(){

        login admin

        expect:
        check create displayed
    }
    def "navigate to quick search and search a data model"(){

        when:
        click quickSearch

        and:'select cancer'
        fill search with 'Cancer Model' and pick first item

        then:
        check rightSideTitle contains "Cancer Model"
    }
    def"navigate to the quick search and search for a data class"(){

        driver.navigate().back()
        click quickSearch
        fill search with "Consent Update" and pick first item

        expect:
        check rightSideTitle contains "Consent Update"


    }
    def" navigate to quick search and search for a data element"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with "ACTH Lower Range" and pick first item

        then:
        check rightSideTitle contains "ACTH Lower Range "

    }
    def" quick search a data type"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with "Boolean" and pick first item

        then:
        check rightSideTitle contains("Boolean")

    }
    def"quick search a Measurement unit"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with "candela" and pick first item

        then:
        check rightSideTitle contains "candela"

    }
    def"quick search a business rule"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with "Clinic Sample Datetime Not Future" and pick first item

        then:
        check rightSideTitle contains "Clinic Sample Datetime Not Future"

    }

    def"quick search an asset"(){

        when:
        driver.navigate().back()
        click quickSearch

        and:
        fill search with "InvestigationsRDInterim-v1.0.0.xsd " and pick first item

        then:
        check rightSideTitle contains "CarePlansCancer-v3.1.3.xsd"


    }


}
