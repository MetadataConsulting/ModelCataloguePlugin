package org.modelcatalogue.core

import org.modelcatalogue.core.pages.DataTypeListPage
import spock.lang.Stepwise

@Stepwise
class DataTypeWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        go "#/"
        loginAdmin()

        when:
        go "#/catalogue/dataType/all"

        then:
        at DataTypeListPage
        waitFor(120) {
            viewTitle.displayed
        }
        waitFor {
            viewTitle.text().trim() == 'Data Type List'
        }
        waitFor {
            actionButton('create-catalogue-element', 'list').displayed
        }

    }

    def "create enum"() {

        when:
        actionButton('create-catalogue-element', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#name').value('New Enum Type')
        $('#enumerated').click()


        waitFor {
            $('table', title: 'Enumerations').displayed
        }

        fillMetadata '01': 'one', '02': 'two'

        actionButton('modal-save-element', 'modal').click()

        then:
        waitFor {
            infTableCell(1, 1, text: 'New Enum Type').displayed
        }
    }

    def "create standard"() {

        when:
        actionButton('create-catalogue-element', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#name').value('New Data Type')

        actionButton('modal-save-element', 'modal').click()

        then:
        waitFor {
            infTableCell(1, 1, text: 'New Data Type').displayed
        }
    }

    def "check it shows up with own detail page"(){
        when:
        waitFor {
            infTableCell(1, 1, text: "New Data Type").displayed
        }

        then:

        infTableCell(1, 1).find('a:not(.inf-cell-expand)').click()

        waitFor(60) {
            subviewTitle.displayed
        }

        subviewTitle.text().trim() == 'New Data Type DRAFT'
    }

}