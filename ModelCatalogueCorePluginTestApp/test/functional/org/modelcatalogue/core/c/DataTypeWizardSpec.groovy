package org.modelcatalogue.core.c

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


    def "filter by name in header"() {
        waitUntilModalClosed()
        when: "the header is expanded"
        $('.inf-table thead .inf-cell-expand').click()

        then: "the name filter is displayed"
        waitFor {
            $('input.form-control', placeholder: 'Filter Name').displayed
        }

        when: "we filter by anatomical side"
        $('input.form-control', placeholder: 'Filter Name').value('anatomical side')

        then: "only one row will be shown"
        waitFor {
            $('.inf-table tbody .inf-table-item-row').size() == 1
        }

        when: "the filter is reset"
        $('input.form-control', placeholder: 'Filter Name').value('')

        then: "we see many rows again"
        waitFor {
            $('.inf-table tbody .inf-table-item-row').size() > 1
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



    def "update metadata"() {
        waitUntilModalClosed()
        when:
        selectTab('ext')

        then:
        waitFor {
            $('button.btn-primary.update-object').disabled
        }
        waitFor {
            $('.btn.add-metadata').displayed
        }

        when:
        noStale({$('.btn.add-metadata')}) {
            it.click()
        }

        fillMetadata(foo: 'bar', one: 'two', free: 'for')

        then:
        waitFor {
            !$('button.btn-primary.update-object').disabled
        }

        when:
        $('button.btn-primary.update-object').click()

        then:
        waitFor {
            $('button.btn-primary.update-object').displayed
        }
        waitFor(30) {
            $('button.btn-primary.update-object').disabled
        }

        when:
        3.times {
            noStale({$('a.soe-remove-row')}) {
                it.click()
            }
        }

        then:
        waitFor {
            !$('button.btn-primary.update-object').disabled
        }

        when:
        $('button.btn-primary.update-object').click()

        then:
        waitFor(30) {
            $('button.btn-primary.update-object').displayed && $('button.btn-primary.update-object').disabled
        }

        when:
        browser.driver.navigate().refresh()

        then:
        waitFor {
            $('.btn.add-metadata').displayed
        }

    }

    def "create new mapping"() {
        waitUntilModalClosed()
        when: "create new mapping action is clicked"
        actionButton('catalogue-element').click()
        actionButton('create-new-mapping').click()


        then: "crate new mapping dialog opens"
        waitFor {
            modalDialog.displayed
            modalHeader.text() == 'Create new mapping for New Data Type'
        }

        when: "value domain is selected"
        dataType = 'xs:boolean'
        selectCepItemIfExists()

        and: "new mapping rule is created"
        mapping = "number(x).asType(Boolean)"

        and: "the create mapping button is clicked"
        modalPrimaryButton.click()

        then: "there is exactly one mapping"
        waitFor {
            totalOf('mappings') == 1
        }
    }

    def "convert value"() {
        waitUntilModalClosed()
        when: "convert action is clicked"
        actionButton('catalogue-element').click()
        actionButton('convert').click()

        then: "modal is shown"
        waitFor {
            modalDialog.displayed
            modalHeader.text() == 'Convert Value from New Data Type'
        }

        when: "truthy value is entered"
        value = '10'

        then: "true is shown"
        waitFor {
            modalDialog.find('pre').text().trim() == 'true'
        }

        when: "falsy value is entered"
        value = '0'

        then: "false is shown"
        waitFor {
            modalDialog.find('pre').text().trim() == 'false'
        }

//        when: "invalid value is entered"
//        value = 'foo'
//
//        then: "INVALID is shown"
//        waitFor {
//            modalDialog.find('pre').text().trim().contains('INVALID')
//        }

        when:
        modalCloseButton.click()

        then:
        waitFor {
            !modalDialog
        }
    }

    def "edit mapping"() {
        waitUntilModalClosed()
        when: "mappings tab selected"
        selectTab('mappings')


        then: "mappings tab is active"
        waitFor {
            tabActive('mappings')
        }

        when:
        toggleInfTableRow(1)
        actionButton('edit-mapping').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        mapping = 'x'
        modalPrimaryButton.click()

        then:
        waitFor {
            infTableCell(1, 2).text().trim() == 'x'
        }
    }


    def "create relationship"() {
        waitUntilModalClosed()
        when: "create relationship action is clicked"
        actionButton('catalogue-element').click()
        actionButton('create-new-relationship').click()

        then: "modal is shown"
        waitFor {
            modalDialog.displayed
        }

        when:
        type    = 'related to'
        element = 'xs:boolean'
        selectCepItemIfExists()


        noStale({ $('.expand-metadata') })  {
            it.click()
        }

        then:
        waitFor {
            $('.metadata-help-block').displayed
        }

        when:
        fillMetadata(modalDialog, foo: 'bar', one: 'two')

        modalPrimaryButton.click()

        then:
        waitFor {
            totalOf('relatedTo') == 1
        }

    }

    def "create relationship from footer action"() {
        waitUntilModalClosed()
        when: "related to tab selected"
        selectTab('relatedTo')


        then: "related to tab is active"
        waitFor {
            tabActive('relatedTo')
        }
        waitFor {
            tableFooterAction.displayed
        }

        when: "click the footer action"
        tableFooterAction.click()

        then: "modal is shown"
        waitFor {
            modalDialog.displayed
        }

        when:
        element = 'xs:string'
        selectCepItemIfExists()

        modalPrimaryButton.click()

        then:
        waitFor {
            totalOf('relatedTo') == 2
        }
    }

    def "remove relationship"() {
        waitUntilModalClosed()
        when:
        toggleInfTableRow(1)
        actionButton('remove-relationship').click()

        then:
        waitFor {
            confirmDialog.displayed
        }

        when:
        confirmOk.click()

        then:
        waitFor {
            totalOf('relatedTo') == 1
        }
    }

    def "remove mapping"() {
        waitUntilModalClosed()
        when:
        selectTab('mappings')
        toggleInfTableRow(1)
        actionButton('remove-mapping').click()

        then:
        waitFor {
            confirmDialog.displayed
        }

        when:
        confirmOk.click()

        then:
        waitFor {
            totalOf('mappings') == 0
        }
    }



    // following is just copy-pasted until we find better way how to run feature methods stepwise
    def "finalize domain"() {
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
        waitFor {
            subviewStatus.text() == 'FINALIZED'
        }

    }

    def "create new version of the domain"() {
        waitUntilModalClosed()
        when: "new version is clicked"
        actionButton('change-element-state').click()
        actionButton('create-new-version').click()

        then: "modal prompt is displayed"
        waitFor {
            confirmDialog.displayed
        }

        when: "ok is clicked"
        confirmOk.click()

        then: "the element new draft version is created"
        waitFor(30) {
            totalOf('history') == 2
        }
        waitFor(30) {
            subviewStatus.text() == 'DRAFT'
        }

    }

    def "merge domain"() {
        waitUntilModalClosed()
        when:
        actionButton('change-element-state').click()
        actionButton('merge').click()

        then:
        waitFor {
            modalDialog.displayed
            modalHeader.text() == 'Merge Data Type New Data Type to another Data Type'
        }

        when: "type is select and confirmed"
        value = 'same name'
        selectCepItemIfExists()

        modalPrimaryButton().click()

        then: "the item is merged and we are redirected to destination domain"
        waitFor {
            subviewTitle.text().trim() == 'Same Name DRAFT'
        }

    }

}