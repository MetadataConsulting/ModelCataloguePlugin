package org.modelcatalogue.core

import spock.lang.IgnoreIf

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class MeasurementUnitWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        login admin

        when:
        select 'Test 2' select "Measurement Units"

        then:
        check rightSideTitle is 'Active Measurement Units'
    }

    def "create new unit"() {
        when:
        click create

        then:
        check modalDialog displayed

        when:
        fill 'name' with 'Foos'
        fill 'symbol' with 'Foo'

        click save

        then:
        check { infTableCell(1, 2, text: 'Foos') } displayed
    }

    def "check the unit shows up with own detail page"() {
        remove messages
        click { infTableCell(1, 2).find('a') }

        expect:
        check rightSideTitle contains 'Foos'
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
