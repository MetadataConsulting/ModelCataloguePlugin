package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.Common
import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class MeasurementUnitWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        login Common.admin

        when:
        select 'Test 2' select "Measurement Units"

        then:
        check Common.rightSideTitle is 'Active Measurement Units'
    }

    def "create new unit"() {
        when:
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill 'name' with 'Foos'
        fill 'symbol' with 'Foo'

        click Common.save

        then:
        check { infTableCell(1, 2, text: 'Foos') } displayed
    }

    def "check the unit shows up with own detail page"(){
        remove Common.messages
        click { infTableCell(1, 2).find('a') }

        expect:
        check Common.rightSideTitle contains 'Foos'
    }

    def "going to metadata tab changes the url"() {
        check { $('li', 'data-tab-name': 'relatedTo') } displayed

        when:
        click { $('li', 'data-tab-name': 'relatedTo').find('a') }

        then:
        waitFor {
            currentUrl.toString().endsWith('/relatedTo')
        }
    }

}
