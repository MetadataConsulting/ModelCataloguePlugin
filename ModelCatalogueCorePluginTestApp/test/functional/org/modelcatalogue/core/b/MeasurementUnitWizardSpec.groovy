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
        check rightSideTitle is 'Foos DRAFT'
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

    def "finalize element"() {
        check backdrop gone

        when: "finalize is clicked"
        click finalize

        then: "modal prompt is displayed"
        check confirm displayed

        when: "ok is clicked"
        click OK

        then: "the element is finalized"
        check subviewStatus is 'FINALIZED'
    }

    def "deprecate the element"() {
        check backdrop gone

        when: "depracete action is clicked"
        click archive

        then: "modal prompt is displayed"
        check confirm displayed

        when: "ok is clicked"
        click OK

        then: "the element is now deprecated"
        check subviewStatus is 'DEPRECATED'

    }

    def "restore the element"() {
        check backdrop gone

        when: "restore action is clicked"
        click archive

        then: "modal prompt is displayed"
        check confirm displayed

        when: "ok is clicked"
        click OK

        then: "the element is now finalized"
        check subviewStatus is 'FINALIZED'
    }

}