package org.modelcatalogue.core

import geb.spock.GebReportingSpec
import org.modelcatalogue.core.pages.MeasurementUnitListPage
import spock.lang.Stepwise

@Stepwise
class MeasurementUnitWizardSpec extends GebReportingSpec {

    def "go to login"() {
        when:
        go "#/catalogue/measurementUnit/all"

        then:
        at MeasurementUnitListPage
        waitFor(120) {
            viewTitle.displayed
        }

        viewTitle.text().trim()     == 'Measurement Unit List'

        when:
        loginAdmin()

        then:
        waitFor {
            actionButton('create-catalogue-element', 'list').displayed
        }

    }

    def "create new unit"() {
        int initialSize = $('.inf-table tbody .inf-table-item-row').size()

        when:
        actionButton('create-catalogue-element', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#name').value('Foos')
        $('#symbol').value('Foo')

        actionButton('modal-save-element', 'modal').click()

        then:
        waitFor {
            $('.inf-table tbody .inf-table-item-row').size() == initialSize + 1
        }
    }

    def "check the unit shows up with own detail page"(){
        when:
        waitFor {
            infTableCell(1, 2, text: "Foos").displayed
        }

        then:

        infTableCell(1, 2).find('a').click()

        waitFor(60) {
            subviewTitle.displayed
        }

        subviewTitle.text().trim() == 'Foos DRAFT'
    }

}