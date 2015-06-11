package org.modelcatalogue.core

import org.modelcatalogue.core.pages.MeasurementUnitListPage
import spock.lang.Stepwise

@Stepwise
class MeasurementUnitWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        go "#/"
        loginAdmin()

        // poor man's fix
        browser.driver.navigate().refresh()

        when:
        go "#/catalogue/measurementUnit/all"

        then:
        at MeasurementUnitListPage
        waitFor(120) {
            viewTitle.displayed
        }
        waitFor {
            viewTitle.text().trim() == 'Measurement Unit List'
        }

        waitFor {
            actionButton('create-catalogue-element', 'list').displayed
        }

    }

    def "create new unit"() {

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
            infTableCell(1, 2, text: 'Foos').displayed
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

    def "going to metadata tab changes the url"() {
        waitFor {
            $('li', 'data-tab-name': 'ext').displayed
        }

        when:
        $('li', 'data-tab-name': 'ext').find('a').click()

        then:
        waitFor {
            currentUrl.toString().endsWith('/ext')
        }
    }

    def "finalize element"() {
        waitUntilModalClosed()
        when: "finalize is clicked"
        actionButton('change-element-state').click()
        actionButton('finalize').click()

        then: "modal prompt is displayed"
        waitFor {
            confirmDialog.displayed
        }

        when: "ok is clicked"
        confirmOk.click()

        then: "the element is finalized"
        waitFor(120) {
            subviewStatus.text() == 'FINALIZED'
        }

    }

    def "deprecate the element"() {
        waitUntilModalClosed()
        when: "depracete action is clicked"
        actionButton('change-element-state').click()
        actionButton('archive').click()

        then: "modal prompt is displayed"
        waitFor {
            confirmDialog.displayed
        }

        when: "ok is clicked"
        confirmOk.click()

        then: "the element is now deprecated"
        waitFor {
            subviewStatus.text() == 'DEPRECATED'
        }

    }

    def "restore the element"() {
        waitUntilModalClosed()
        when: "restore action is clicked"
        actionButton('change-element-state').click()
        actionButton('archive').click()

        then: "modal prompt is displayed"
        waitFor {
            confirmDialog.displayed
        }

        when: "ok is clicked"
        confirmOk.click()

        then: "the element is now finalized"
        waitFor {
            subviewStatus.text() == 'FINALIZED'
        }

    }

}