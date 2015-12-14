package org.modelcatalogue.core.b

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class DataModelWizardSpec extends AbstractModelCatalogueGebSpec {

    static final String classificationWizzard = 'div.create-classification-wizard'
    static final String name = 'div.create-classification-wizard #name'
    static final String description ='div.create-classification-wizard #description'
    static final String modelCatalogueId ='div.create-classification-wizard #modelCatalogueId'
    static final String stepImports = "#step-imports"
    static final String stepFinish = "#step-finish"
    static final String exitButton = "#exit-wizard"


    def "go to login"() {
        login admin

        expect:
        waitFor(120) { browser.title == 'Data Models' }
    }

    def "add new data model"() {
        click createActionInInfiniteList

        expect: 'the model dialog opens'
        check classificationWizzard displayed

        when:
        fill name with "New Data Model"
        fill modelCatalogueId with "http://www.example.com/${UUID.randomUUID().toString()}"
        fill description with "Description of Data Model"

        then:
        check stepImports enabled

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:
        fill name with 'NHIC'
        selectCepItemIfExists()


        and:
        click stepFinish

        then:
        check 'div.messages-panel span', text: "Data Model New Data Model created"

        when:
        click exitButton

        then:
        check rightSideTitle is 'New Data Model DRAFT'
        check tabTotal('imports') is '1'
    }


}