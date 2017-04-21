package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.openqa.selenium.Keys
import static org.modelcatalogue.core.geb.Common.*


class CreateDataModelSpec extends AbstractModelCatalogueGebSpec {
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String version = "input#semanticVersion"
    private static final String catalogueId = "input#modelCatalogueId"
    private static final String policies = "input#dataModelPolicy"
    private static final String importData = "input#name"
    private static final String finish = "button#step-finish"

    def "do create data model"() {

        when:
            // enter username , password
            login curator

        then:
            noExceptionThrown()

        when:
            // click on create
            click create
            // type a name . please change value
            fill name with newModelName
            fill version with versionElement
            fill catalogueId with catalogue
            fill policies with policy and pick first item

        then:
            check policies displayed

        when:
            // switch to import
            $(policies) << Keys.ENTER

        then:
            // verify import button present
            check "button#step-imports" is "2. Imports"

        when:
            // type c in the search box and selectRelation first item
            fill importData with text and pick first item

            // click on finish
            click finish


        then:
            // please this unique for this data . change string data for your own data or use noexcep
           // check '#summary' is "Data Model $newModelName created"
             noExceptionThrown()


        cleanup:
            // click on close
            click modalCloseButton
            click modalPrimaryButton


        where:
        newModelName                                | versionElement | catalogue                            | policy   | text
        "my Test ${System.currentTimeMillis()}"    | "2.1.28${System.currentTimeMillis()}"| "MT-${System.currentTimeMillis()}"   | "c"      | "c"



    }
}
