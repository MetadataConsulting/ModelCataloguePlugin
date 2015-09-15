package org.modelcatalogue.core.a

import org.modelcatalogue.core.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.pages.ModalTreeViewPage
import org.openqa.selenium.Keys
import spock.lang.Stepwise

@Stepwise
class DataClassWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        go "#/"
        loginAdmin()

        when:
        go "#/catalogue/dataClass/all"

        then:
        at ModalTreeViewPage
        waitFor(120) {
            !$('#jserrors').displayed && viewTitle.displayed
        }
        waitFor {
            viewTitle.text()?.trim() == 'Data Classes'
        }
        waitFor {
            subviewTitle.text()?.trim()  == 'NHIC Datasets FINALIZED'
        }

        waitFor {
            addModelButton.displayed
        }

    }

    def "Add new data class"() {
        when: 'I click the add data class button'
        addModelButton.click()


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

        and: 'the classifications step is clicked'
        stepClassifications.click()

        then:
        waitFor {
            stepClassifications.hasClass('btn-primary')
        }

        when: 'the classification is selected'
        name = 'TEST DATA MODEL'
        selectCepItemIfExists()
        name << Keys.ENTER

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
            $('span.catalogue-element-treeview-name', text: "New 1").displayed
        }

        waitUntilModalClosed(30)
    }
    def "filter by classification"() {
        when:
        go "#/catalogue/dataModel/all"

        then:
        waitFor {
            $('h3', title: 'XMLSchema').displayed
        }

        when:
        $('h3', title: 'XMLSchema').find('a').click()

        then:
        waitFor {
            menuItem('currentDataModel').text().contains('XMLSchema')
        }

        when:
        go "#/catalogue/dataClass/all?status=draft"

        then:
        waitFor {
            !$('span.catalogue-element-treeview-name', text: "New 1").displayed
        }

        when:
        waitFor {
            menuItem('currentDataModel').displayed
        }
        menuItem('currentDataModel').click()

        then:
        waitFor {
            menuItem('all-data-models', '').displayed
        }

        when:
        menuItem('all-data-models', '').click()

        then:
        waitFor {
            $('h3', title: 'XMLSchema').displayed
        }
        waitFor {
            menuItem('currentDataModel').text().contains('All Data Models')
        }

        when:
        go "#/catalogue/dataClass/all?status=draft"

        then:
        waitFor {
            $('span.catalogue-element-treeview-name', text: "New 1").displayed
        }

    }

    def "open the detail view"() {
        waitFor(30) {
            $('a.catalogue-element-treeview-link', title: "New").displayed
        }

        when: 'the item is clicked'
        $('a.catalogue-element-treeview-link', title: "New").click()

        then:
        waitFor {
            subviewTitle.text().trim() == 'New DRAFT'
        }

        totalOf('parentOf') == 2
        totalOf('contains') == 1


    }


    def "Add another data class"(){
        when:
        go "#/catalogue/dataClass/all"

        then:
        at ModalTreeViewPage
        waitFor(120) {
            viewTitle.displayed
        }

        when: 'I click the add data class button'
        addModelButton.click()


        then: 'the data class dialog opens'
        waitFor {
            modelWizard.displayed
        }

        when: 'the data class details are filled in'
        name = "Another New"

        and: 'the classifications step is clicked'
        stepClassifications.click()

        then:
        waitFor {
            stepClassifications.hasClass('btn-primary')
        }

        when: 'the classification is selected'
        name = 'TEST DATA MODEL'
        selectCepItemIfExists()
        name << Keys.ENTER

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
            $('span.catalogue-element-treeview-name', text: "Another New 1").displayed
        }

        when: "click the footer action"
        $('span.catalogue-element-treeview-name', text: "Another New 1").click()
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
            $('span.catalogue-element-treeview-name', text: "Another New 1").parent().parent().find('.badge').text() == '1'
        }

    }

    def "edit child data class"() {
        expect:
        actionButton('edit-catalogue-element').displayed

        when:
        actionButton('edit-catalogue-element').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        modalDialog.find('#name').value('Changed Name')
        modalDialog.find("button.btn-success").click()

        then: "same number of children are still shown"
        waitFor {
            $('span.catalogue-element-treeview-name', text: "Changed Name 1").parent().parent().find('.badge').text() == '1'
        }
    }

}