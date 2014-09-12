package org.modelcatalogue.core

import geb.spock.GebSpec
import org.modelcatalogue.core.pages.ModalTreeViewPage

class SmokeSpec extends GebSpec {

    def "go to login"() {
        when:
        go "#/catalogue/model/all"

        then:
        at ModalTreeViewPage
        waitFor {
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

        when:
        addModelButton.click()


        then:
        waitFor {
            modelWizard.displayed
        }

        when:
        name        = "New"
        description = "Description"

        saveButton.click()


        then:
        waitFor {
            $('div.messages-panel span', text: "Model New created").displayed
        }

    }

}