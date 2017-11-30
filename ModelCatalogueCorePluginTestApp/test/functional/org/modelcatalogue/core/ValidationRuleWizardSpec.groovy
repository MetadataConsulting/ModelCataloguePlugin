package org.modelcatalogue.core

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class ValidationRuleWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        login admin

        when:
        select 'Test 3' select "Business Rules"

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
        check { infTableCell(1, 1) } contains 'Test Validation Rule'
    }

    def "check the unit shows up with own detail page"() {
        check closeGrowlMessage gone
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        expect:
        check rightSideTitle contains 'Test Validation Rule'
    }

}
