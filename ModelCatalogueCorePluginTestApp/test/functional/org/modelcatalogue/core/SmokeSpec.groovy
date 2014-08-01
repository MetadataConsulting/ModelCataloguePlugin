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
        viewTitle.text().trim()     == 'Model Hierarchy'
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
            basicEditDialog.displayed
        }

        when:
        name        = "New Model"
        description = "New Model's Description"

        saveButton.click()

        then:
        $('blockquote').text() == "New Model's Description"

        cleanup:
        logout()
    }

}