package org.modelcatalogue.core

import org.modelcatalogue.core.pages.ModalTreeViewPage
import org.openqa.selenium.Keys
import spock.lang.Stepwise

@Stepwise
class ModelWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        go "#/"
        loginAdmin()

        when:
        go "#/catalogue/model/all"

        then:
        at ModalTreeViewPage
        waitFor(120) {
            !$('#jserrors').displayed && viewTitle.displayed
        }
        waitFor {
            viewTitle.text()?.trim() == 'Models'
        }
        waitFor {
            subviewTitle.text()?.trim()  == 'NHIC Datasets FINALIZED'
        }

        waitFor {
            addModelButton.displayed
        }

    }

    def "Add new model"() {
        when: 'I click the add model button'
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
        name = 'This should create new child model'

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
        name = 'TEST CLASSIFICATION'
        selectCepItemIfExists()
        name << Keys.ENTER

        and: 'finish is clicked'
        stepFinish.click()

        then: 'the model is saved'
        waitFor {
            $('div.messages-panel span', text: "Model New created").displayed
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
        expect:
        menuItem('classifications', 'navigation-bottom-left').displayed

        when:
        menuItem('classifications', 'navigation-bottom-left').click()

        then:
        waitFor {
            modalDialog.displayed
        }


        when:
        $('#includes').value('xmlschema')
        selectCepItemIfExists()
        modalPrimaryButton.click()

        then:
        waitFor {
            !$('span.catalogue-element-treeview-name', text: "New 1").displayed && menuItem('classifications', 'navigation-bottom-left').text().contains('XMLSchema')
        }

        when:
        menuItem('classifications', 'navigation-bottom-left').click()

        then:
        waitFor {
            modalDialog.displayed
        }
        waitFor {
            modalDialog.find("#remove-tag-0").displayed
        }

        when:
        modalDialog.find("#remove-tag-0").click()
        modalPrimaryButton.click()

        then:
        waitFor {
            !modalDialog.find("#remove-tag-0").displayed
        }
        waitFor {
            $('span.catalogue-element-treeview-name', text: "New 1").displayed && menuItem('classifications', 'navigation-bottom-left').text().contains('All Classifications')
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


    def "Add another model"(){
        when:
        go "#/catalogue/model/all"

        then:
        at ModalTreeViewPage
        waitFor(120) {
            viewTitle.displayed
        }

        when: 'I click the add model button'
        addModelButton.click()


        then: 'the model dialog opens'
        waitFor {
            modelWizard.displayed
        }

        when: 'the model details are filled in'
        name = "Another New"

        and: 'finish is clicked'
        stepFinish.click()

        then: 'the model is saved'
        waitFor {
            $('div.messages-panel span', text: "Model Another New created").displayed
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

    def "edit child model"() {
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