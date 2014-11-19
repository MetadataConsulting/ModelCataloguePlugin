package org.modelcatalogue.core

import geb.spock.GebReportingSpec
import geb.spock.GebSpec
import org.modelcatalogue.core.pages.ModalTreeViewPage
import spock.lang.Stepwise

@Stepwise
class ModelWizardSpec extends GebReportingSpec {

    def "go to login"() {
        when:
        go "#/catalogue/model/all"

        then:
        at ModalTreeViewPage
        waitFor(120) {
            viewTitle.displayed
        }

        viewTitle.text().trim()     == 'Models'
        subviewTitle.text().trim()  == 'NHIC Datasets'

        when:
        loginAdmin()

        then:
        waitFor {
            addModelButton.displayed
        }

    }
        def "Add new model"(){

        when: 'I click the add model button'
        addModelButton.click()


        then: 'the model dialog opens'
        waitFor {
            modelWizard.displayed
        }

        when: 'the model details are filled in'
        name        = "New"
        description = "Description"

        and: 'save button clicked'
        saveButton.click()


        then: 'the model is saved'
        waitFor {
            $('div.messages-panel span', text: "Model New created").displayed
        }
        when:
        exitButton.click()

        then:
        waitFor {
            $('span.catalogue-element-treeview-name', text: "New").displayed
        }






    }

}