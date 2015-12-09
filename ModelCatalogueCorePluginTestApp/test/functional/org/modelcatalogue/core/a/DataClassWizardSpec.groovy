package org.modelcatalogue.core.a

import org.modelcatalogue.core.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.pages.ModalTreeViewPage
import org.openqa.selenium.Keys
import spock.lang.Stepwise

@Stepwise
class DataClassWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        loginAdmin()

        openDataModel('Test 2')
        selectInTree "Test 2"


        waitFor {
            menuItem('catalogue-element', 'item').displayed
        }

        menuItem('catalogue-element', 'item').click()

        waitFor {
            menuItem('add-import', '').displayed
        }

        menuItem('add-import', '').click()

        noStale({ $('div.modal #elements') }) {
            it.value('nhic')
        }

        selectCepItemIfExists()

        noStale({ $('div.modal .btn-primary') }) {
            it.click()
        }

        waitFor {
            !$('div.modal').displayed
        }

        selectInTree "Data Classes"

        expect:
        at ModalTreeViewPage

        waitFor(120) {
            !$('#jserrors').displayed && $('h3').displayed
        }
        waitFor {
            subviewTitle.text()?.trim()?.contains('Data Class List')
        }
    }

    def "Add new data class"() {
        waitFor {
            menuItem('create-catalogue-element', 'list').displayed
        }

        when:
        menuItem('create-catalogue-element', 'list').click()

        then: 'the model dialog opens'
        waitFor {
            modelWizard.displayed
        }

        when: 'the model details are filled in'
        name = "New"
        modelCatalogueId = "http://www.example.com/${UUID.randomUUID().toString()}"
        description = "Description"

        then: 'metadata step is not disabled'
        waitFor {
            !stepMetadata.disabled
        }

        when: 'metadata step is clicked'
        stepMetadata.click()

        then:
        waitFor {
            stepMetadata.hasClass('btn-primary')
        }

        when: 'metadata are filled in'
        fillMetadata foo: 'bar', one: 'two'


        and: 'children step is clicked'
        stepChildren.click()

        then:
        waitFor {
            stepChildren.hasClass('btn-primary')
        }

        when: 'child metadata are filled in'
        fillMetadata 'Min Occurs': '1', 'Max Occurs': '10'

        and: 'the child is selected'
        $('.search-for-more-icon').click()
        $('.modal-body .input-group-lg input').value('patient')

        noStale({ $('.list-group-item.item-found') }) {
            it.click()
        }

        and: 'create child from scratch and leave it filled in'
        name = 'This should create new child data class'

        and: 'elements step is clicked'
        stepElements.click()

        then:
        waitFor {
            stepElements.hasClass('btn-primary')
        }

        when: 'element metadata are filled in'
        fillMetadata 'Min Occurs': '2', 'Max Occurs': '25'

        and: 'the element is selected'
        name = 'nhs'
        selectCepItemIfExists()

        and: 'finish is clicked'
        stepFinish.click()

        then: 'the data class is saved'
        waitFor(60) {
            $('div.messages-panel span', text: "Data Class New created").displayed
        }
        when:
        exitButton.click()

        then:
        waitFor {
            $('span.catalogue-element-treeview-name', text: startsWith("New")).displayed
        }

        waitUntilModalClosed(30)
    }

    def "Add another data class"(){
        waitFor {
            menuItem('create-catalogue-element', 'list').displayed
        }

        when:
        menuItem('create-catalogue-element', 'list').click()


        then: 'the data class dialog opens'
        waitFor {
            modelWizard.displayed
        }

        when: 'the data class details are filled in'
        name = "Another New"


        and: 'finish is clicked'
        stepFinish.click()

        then: 'the data class is saved'
        waitFor {
            $('div.messages-panel span', text: "Data Class Another New created").displayed
        }
        when:
        exitButton.click()

        then:
        waitFor {
            $('span.catalogue-element-treeview-name', text: startsWith("Another New")).displayed
        }

        when: "click the footer action"
        $('span.catalogue-element-treeview-name', text: startsWith("Another New")).click()

        selectTab('contains')

        tableFooterAction.click()

        then: "modal is shown"
        waitFor {
            modalDialog.displayed
        }

        when:
        type    = 'parent of'
        element = 'demographics'
        selectCepItemIfExists()

        modalPrimaryButton.click()

        then: 'the number of children of Another New must be 1'
        waitFor {
            $('span.catalogue-element-treeview-name', text: startsWith("Another New")).parent().parent().find('.badge').text() == '1'
        }

    }

    def "edit child data class"() {
        expect:
        menuItem('edit-catalogue-element', 'item').displayed

        when:
        menuItem('edit-catalogue-element', 'item').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        modalDialog.find('#name').value('Changed Name')
        modalDialog.find("button.btn-success").click()

        then: "same number of children are still shown"
        waitFor {
            $('span.catalogue-element-treeview-name', text: startsWith("Changed Name")).parent().parent().find('.badge').text() == '1'
        }
    }

}