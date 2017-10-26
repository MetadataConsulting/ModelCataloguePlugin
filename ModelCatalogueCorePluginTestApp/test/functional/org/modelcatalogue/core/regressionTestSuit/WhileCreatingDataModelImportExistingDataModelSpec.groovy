package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title
import static org.modelcatalogue.core.gebUtils.Common.*



@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1815")
class WhileCreatingDataModelImportExistingDataModelSpec extends AbstractModelCatalogueGebSpec {

    private static final String createButton="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String  importButton="button#step-imports"
    private static final String  textPresent="#imports > form > div > label"
    private static final String  finishButton="button#step-finish"
    private static final String  closeButton="div.modal-footer>button:nth-child(2)"
    private static final String  dataModelMenuBar="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static  Actions action = null



    def" login to model catalogue and click on the create button"(){


        when:
        loginAdmin()

        then:
        check 'span.mc-name' contains'Model Catalogue'
    }

    def"While creating a data model, import an existing data model"(){


        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'GEO1'

        then:
        check importButton enabled

        when:
        click importButton

        then:
        check textPresent contains 'Import Existing Data Models'


        when:
        fill '#name' with 'Cancer' and pick first item

        then:
        check 'span.with-pointer' isDisplayed()


        when:
        click finishButton

        then:
        check 'div#summary>h4' is 'Data Model GEO1 created'

        when:
        click closeButton

        then:
        check rightSideTitle contains 'GEO1'

    }

    def"verify that the imported Data Model is present"(){

        when:
        selectInTree 'Imported Data Models'

        then:
        check{infTableCell(1,1)} contains 'Cancer Model'
    }


    def" delete the created Data Model"(){


        when:
        selectInTree 'GEO1'

        and:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model GEO1?'

        and:
        click modalPrimaryButton
    }
}
