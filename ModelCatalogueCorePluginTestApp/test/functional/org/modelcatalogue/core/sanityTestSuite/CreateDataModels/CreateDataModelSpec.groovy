package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.openqa.selenium.Keys
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.*

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class CreateDataModelSpec extends AbstractModelCatalogueGebSpec {
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String version = "input#semanticVersion"
    private static final String catalogueId = "input#modelCatalogueId"
    private static final String policies = "input#dataModelPolicy"
    private static final String importData = "input#name"
    private static final String finish = "button#step-finish"
    private static final String deleteButton = "a#delete-menu-item-link>span:nth-child(3)"
    private static final String dataModelButton ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"

    def "do create data model"() {

        when:
            login curator

        then:
            check create displayed

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
             noExceptionThrown()


        cleanup:
            // click on close
            click modalCloseButton
            click modalPrimaryButton


        where:
        newModelName         | versionElement | catalogue | policy   | text
        "TESTING_DATA_MODEL" | "2.1.28"       | "MT-234"  | "c"      | "c"

    }

    def"delete the created data model "(){

        when:
        refresh browser
        Thread.sleep(4000L)
        select 'TESTING_DATA_MODEL'

        and:' navigate to the top menu and click on the data model button'
        click dataModelButton

        and:'select the delete button'
        click deleteButton

        and:'confirm'
        click modalPrimaryButton

        then:
        noExceptionThrown()
    }
}
