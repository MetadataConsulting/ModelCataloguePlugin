package org.modelcatalogue.core

import org.modelcatalogue.core.pages.ClassificationListPage
import spock.lang.Stepwise

@Stepwise
class ClassificationWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        go "#/"
        loginAdmin()

        when:
        go "#/catalogue/classification/all"

        then:
        at ClassificationListPage
        waitFor(120) {
            viewTitle.displayed
        }
        waitFor(120) {
            viewTitle.text().trim() == 'Classification List'
        }
        waitFor {
            actionButton('create-catalogue-element', 'list').displayed
        }
    }

    def "add new classification"() {
        when:
        actionButton('create-catalogue-element', 'list').click()


        then: 'the model dialog opens'
        waitFor {
            classificationWizzard.displayed
        }

        when:
        name = "New Classification"
        modelCatalogueId = "http://www.example.com/${UUID.randomUUID().toString()}"
        description = "Description of Classification"

        then:
        waitFor {
            !stepElements.disabled
        }

        when:
        stepElements.click()

        then:
        waitFor {
            stepElements.hasClass('btn-primary')
        }

        and:
        stepElements.click()

        then:
        waitFor {
            stepElements.hasClass('btn-primary')
        }

        when:
        name = 'nhs'
        selectCepItemIfExists()


        and:
        stepFinish.click()

        then:
        waitFor {
            $('div.messages-panel span', text: "Classification New Classification created").displayed
        }
        when:
        exitButton.click()

        then:
        waitFor {
            infTableCell(1, 2, text: 'New Classification').displayed
        }

    }


}