package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.Common
import spock.lang.Stepwise

@Stepwise
class ValidationRuleWizardSpec extends AbstractModelCatalogueGebSpec {

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
        fill 'name' with 'FirstTestSpec Validation Rule'

        click Common.save

        then:
        check { infTableCell(1, 1) } contains 'FirstTestSpec Validation Rule'
    }

    def "check the unit shows up with own detail page"(){
        check Common.closeGrowlMessage gone
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        expect:
        check Common.rightSideTitle contains 'FirstTestSpec Validation Rule'
    }

}
