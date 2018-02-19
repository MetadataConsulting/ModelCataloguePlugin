package org.modelcatalogue.core.regression.businessrule.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
class MET1488_BusinessRuleWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        login Common.admin

        when:
        select 'Test 3' select "Business Rules"

        then:
        check Common.rightSideTitle is 'Active Validation Rules'
    }

    def "create new validation rule"() {
        when:
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill 'name' with 'Test Validation Rule'

        click Common.save

        then:
        check { infTableCell(1, 1) } contains 'Test Validation Rule'
    }

    def "check the unit shows up with own detail page"() {
        check Common.closeGrowlMessage gone
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        expect:
        check Common.rightSideTitle contains 'Test Validation Rule'
    }

}
