package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.gebUtils.Common.getDelete
import static org.modelcatalogue.core.gebUtils.Common.getItem
import static org.modelcatalogue.core.gebUtils.Common.getModalHeader
import static org.modelcatalogue.core.gebUtils.Common.getModalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.getNameLabel
import static org.modelcatalogue.core.gebUtils.Common.getPick
import static org.modelcatalogue.core.gebUtils.Common.getRightSideTitle




@Stepwise @Title("")
class CreateRelationshipSynonymNoAlertSpec extends AbstractModelCatalogueGebSpec {



    private static final String createButton="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String  importButton="button#step-imports"
    private static final String  textPresent="#imports > form > div > label"
    private static final String  finishButton="button#step-finish"
    private static final String  closeButton="div.modal-footer>button:nth-child(2)"
    private static final String  dataModelMenuBar="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  createRelationshipLink="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  change ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
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
        fill nameLabel with 'GEO2'

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
        check 'div#summary>h4' is 'Data Model GEO2 created'

        when:
        click closeButton

        then:
        check rightSideTitle contains 'GEO2'

    }

    def" create a relationship Synonym "(){

        when:
        click dataModelMenuBar
        click createRelationshipLink

        then:
        check 'h4.ng-binding' is 'GEO2'

        when:
        fill 'select#type' with'is synonym for'
        fill 'input#element' with'MET-523' and pick first item
        click 'input#current-data-model-only-0'
        click modalPrimaryButton

        then:
        check change contains 'is synonym for'

    }


    def" delete the created data model"(){

        when:
        selectInTree 'GEO2'

        then:
        check rightSideTitle contains 'GEO2'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model GEO2?'

        and:
        click modalPrimaryButton
    }


}
