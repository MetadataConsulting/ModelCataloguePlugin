package org.modelcatalogue.core.b

import org.modelcatalogue.core.AbstractModelCatalogueGebSpec
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

    def "create reference"() {

        when:
        actionButton('create-catalogue-element', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#name').value('New Reference Type')

        classifications = "NT1"
        selectCepItemIfExists()

        $('#pickReferenceType').click()


        waitFor {
            $('input#dataClass').displayed
        }

        noStale({$('input#dataClass')}) {
            it.value('DEMOGRAPHICS')
        }

        selectCepItemIfExists()

        actionButton('modal-save-element', 'modal').click()

        then:
        waitFor {
            infTableCell(1, 1, text: 'New Reference Type').displayed
        }
    }

    def "create primitive"() {

        when:
        actionButton('create-catalogue-element', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#name').value('New Primitive Type')

        classifications = "NT1"
        selectCepItemIfExists()

        $('#pickPrimitiveType').click()


        waitFor {
            $('input#measurementUnit').displayed
        }

        noStale({$('input#measurementUnit')}) {
            it.value('celsius')
        }

        selectCepItemIfExists()

        actionButton('modal-save-element', 'modal').click()

        then:
        waitFor {
            infTableCell(1, 1, text: 'New Primitive Type').displayed
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

        classifications = "NT1"
        selectCepItemIfExists()

        $('#pickEnumeratedType').click()


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

        classifications = "NT1"
        selectCepItemIfExists()

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