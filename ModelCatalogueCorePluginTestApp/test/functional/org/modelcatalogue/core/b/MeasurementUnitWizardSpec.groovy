package org.modelcatalogue.core.b

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class MeasurementUnitWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        login admin

        when:
        select('Test 2') / 'Test 2' / "Measurement Units"

        then:
        check rightSideTitle is 'Measurement Unit List'
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

    def "check the unit shows up with own detail page"(){
        click { infTableCell(1, 2).find('a') }

        expect:
        check rightSideTitle is 'Foos Test 2 rev1'
    }

    def "going to metadata tab changes the url"() {
        check { $('li', 'data-tab-name': 'ext') } displayed

        when:
        click { $('li', 'data-tab-name': 'ext').find('a') }

        then:
        waitFor {
            currentUrl.toString().endsWith('/ext')
        }
    }

}