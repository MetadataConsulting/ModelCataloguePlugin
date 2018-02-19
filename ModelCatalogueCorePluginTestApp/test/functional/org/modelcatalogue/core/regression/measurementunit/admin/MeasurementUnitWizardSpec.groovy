package org.modelcatalogue.core.regression.measurementunit.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import spock.lang.IgnoreIf
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class MeasurementUnitWizardSpec extends AbstractModelCatalogueGebSpec {


    /*
    * NEED TO CREATE A MANUAL TEST CASE IN ZEPHYR
    *
    * */

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

    def "check the unit shows up with own detail page"() {
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
