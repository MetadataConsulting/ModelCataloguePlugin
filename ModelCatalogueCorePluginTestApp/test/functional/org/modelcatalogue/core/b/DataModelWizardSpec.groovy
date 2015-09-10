package org.modelcatalogue.core.b

import org.modelcatalogue.core.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.pages.DataModelListPage
import spock.lang.Stepwise

@Stepwise
class DataModelWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        go "#/"
        loginAdmin()

        when:
        go "#/catalogue/dataModel/all"

        then:
        at DataModelListPage

        waitFor(120) {
           browser.title == 'Data Models'
        }
    }

    def "add new data model"() {
        waitFor {
            $('a.infinite-list-create-action').displayed
        }
        when:
        $('a.infinite-list-create-action').click()


        then: 'the model dialog opens'
        waitFor {
            classificationWizzard.displayed
        }

        when:
        name = "New Data Model"
        modelCatalogueId = "http://www.example.com/${UUID.randomUUID().toString()}"
        description = "Description of Data Model"

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
            $('div.messages-panel span', text: "Data Model New Data Model created").displayed
        }
        when:
        exitButton.click()

        then:
        waitFor {
            menuItem('currentDataModel').text().contains('New Data Model (draft)')
        }

    }


}