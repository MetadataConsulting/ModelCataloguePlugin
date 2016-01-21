package org.modelcatalogue.core.a

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.pages.ModalTreeViewPage
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.admin
import static org.modelcatalogue.core.geb.Common.closeGrowlMessage
import static org.modelcatalogue.core.geb.Common.modalSuccessButton

@Stepwise
class DataClassWizardSpec extends AbstractModelCatalogueGebSpec {

    CatalogueAction edit = CatalogueAction.runLast('item-detail', 'edit-catalogue-element')

    def "go to login"() {
        login admin

        select('Test 2') / "Test 2"

        addDataModelImport 'nhic'

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
        click edit

        expect:
        check modalDialog displayed

        when:
        fill 'name' with 'Changed Name'

        click modalSuccessButton

        then: "same number of children are still shown"
        check modalDialog gone
        check closeGrowlMessage gone

        check {
            $('span.catalogue-element-treeview-name', text: startsWith("Changed Name")).parent().parent().find('.badge')
        } is '1'
    }

}