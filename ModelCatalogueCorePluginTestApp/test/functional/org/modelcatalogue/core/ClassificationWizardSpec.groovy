package org.modelcatalogue.core

import geb.spock.GebReportingSpec
import org.modelcatalogue.core.pages.ClassificationListPage
import spock.lang.Stepwise

@Stepwise
class ClassificationWizardSpec extends GebReportingSpec {

    def "go to login"() {
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


        when:
        loginAdmin()

        then:
        waitFor {
            actionButton('create-catalogue-element', 'list').displayed
        }
    }

    def "add new classification"() {
        int initialSize = $('.inf-table tbody .inf-table-item-row').size()

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
            $('.inf-table tbody .inf-table-item-row').size() == initialSize + 1
        }

    }


}