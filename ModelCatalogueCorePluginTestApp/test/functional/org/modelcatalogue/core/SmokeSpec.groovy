package org.modelcatalogue.core

import geb.spock.GebSpec
import org.modelcatalogue.core.pages.ModalTreeViewPage

class SmokeSpec extends GebSpec {

    def "go to login"() {
        when:
        go ""

        then:
        title                       == "Model Catalogue Demo App"
        at ModalTreeViewPage
        waitFor {
            viewTitle.displayed
        }
        viewTitle.text().trim()     == 'Models'
        subviewTitle.text().trim()  == 'Another root #000 Data Elements'

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
        waitFor {
            $('blockquote').displayed
        }

        then:
        $('blockquote').text() == "Description"

        cleanup:
        logout()
    }

}