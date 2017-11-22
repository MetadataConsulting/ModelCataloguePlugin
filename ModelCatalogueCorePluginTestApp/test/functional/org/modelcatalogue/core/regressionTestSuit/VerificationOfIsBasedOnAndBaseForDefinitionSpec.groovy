package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.gebUtils.Common.delete
import static org.modelcatalogue.core.gebUtils.Common.getNameLabel
import static org.modelcatalogue.core.gebUtils.Common.getRightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.modelCatalogueId

@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1658")
class VerificationOfIsBasedOnAndBaseForDefinitionSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String  importButton="button#step-imports"
    private static final String   cancelButton="button.btn-warning"
    private static final String  finishButton="button#step-finish"
    private static final String  closeButton="div.modal-footer>button:nth-child(2)"
    private static final String  dataModelMenuBar="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  createRelationshipLink="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static  Actions action = null



    def" Login to model catalogue and click on the create button"(){


        when:
        loginAdmin()

        then:
        check 'span.mc-name' contains'Model Catalogue'
    }

    def" Create a data model"(){


        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'GEO3'
        fill modelCatalogueId with 'MD-123'

        then:
        check importButton enabled

        when:
        click finishButton

        then:
        check 'div#summary>h4' is 'Data Model GEO3 created'

        when:
        click closeButton

        then:
        check rightSideTitle contains 'GEO3 MD-123@0.0.1'

    }

    def"Verification of Is Based on and Base For definition"(){

        when:
        click dataModelMenuBar
        click createRelationshipLink

        then:
         check 'h4.ng-binding' is 'GEO3'

        when:
        fill 'select#type' with 'is based on'

        then:
        check 'p.ng-binding' contains  'A Catalogue Element can be based on multiple Catalogue Elements of the same type.'

        when:
        fill 'select#type' with 'is base for'

        then:
        check 'p.ng-binding' is 'A Catalogue Element can be base for multiple Catalogue Elements of the same type.'
    }



    def"Delete the create Data Model"(){


        when:
        click cancelButton

        then:
        check rightSideTitle contains 'GEO3 MD-123@0.0.1'


        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model GEO3?'

        and:
        click modalPrimaryButton


    }

}
