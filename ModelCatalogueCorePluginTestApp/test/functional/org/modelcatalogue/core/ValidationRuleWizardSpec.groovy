package org.modelcatalogue.core

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class ValidationRuleWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        login admin

        when:
        select 'Test 2' select "Validation Rules"

        then:
        check rightSideTitle is 'Active Validation Rules'
    }

    def "create new validation rule"() {
        when:
        click create

        then:
        check modalDialog displayed

        when:
        fill 'name' with 'Test Validation Rule'

        click save

        then:
        check { infTableCell(1, 2, text: 'Test Validation Rule') } displayed
    }

    def "check the unit shows up with own detail page"(){
        check closeGrowlMessage gone
        click { infTableCell(1, 2).find('a') }

        expect:
        check rightSideTitle contains 'Test Validation Rule'
    }

}
