package org.modelcatalogue.core.b

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

/**
 * Created by david on 02/11/14.
 */
@Stepwise
class DataElementWizardSpec extends AbstractModelCatalogueGebSpec {

    def "login and select Data Element"() {
        login admin
        select('Test 1') / 'Test 1' / 'Data Elements'

        expect:
        check rightSideTitle is 'Data Element List'
    }

    def "Add new data element"() {
        when: 'I click the add model button'
        click create

        then: 'the data element dialog opens'
        check wizard displayed

        when:
        fill name with "NewDE1"
        fill description with "NT1 Description"

        and: 'save button clicked'
        click save

        then: 'the data element is saved and displayed at the top of the table'
        check nameInTheFirstRow, text: "NewDE1" displayed
    }

    def "Check the data element shows up with own details"() {
        expect:
        check backdrop gone

        when: 'Data Element is located'
        check nameInTheFirstRow, text: "NewDE1" displayed

        then: 'Click the element'
        click firstRowLink
        check rightSideTitle contains 'NewDE1 Test 1'
    }

    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check detailSectionFormItem contains 'Form (Item)'
        check detailSectionFormItemContent gone

        when: "Click the title"
        click detailSectionFormItem

        then: "Content is displayed"
        check detailSectionFormItemContent displayed
    }
}
