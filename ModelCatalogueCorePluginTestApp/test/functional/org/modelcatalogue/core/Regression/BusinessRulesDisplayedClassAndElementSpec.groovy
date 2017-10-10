package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title
import static org.modelcatalogue.core.geb.Common.*


@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1382")
class BusinessRulesDisplayedClassAndElementSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String  importStep="button#step-imports"
    private static final String  finishButton="button#step-finish"
    private static final String  closeButton="div.modal-footer>button:nth-child(2)"
    private static final String  metadataStep="button#step-metadata"
    private static final String   exitButton="button#exit-wizard"
    private static final String    dataClassConfirmation="form.ng-valid>div:nth-child(4)>div>span>span>span"
    private static final String   dataElementConfirmation="form.ng-valid>div:nth-child(5)>div>span>span>span"
    private static final String    validationRule="td.col-md-4>span>span>a"
    private static final String    dataElementText="form.ng-pristine>div:nth-child(7)>div>ng-include>div>div:nth-child(3)>table>tbody>tr>td:nth-child(1)"
    private static final String    dataClassText="form.ng-pristine>div:nth-child(6)>div>ng-include>div>div:nth-child(3)>table>tbody>tr>td:nth-child(1)"
    private static final String dataModelMenuBar ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static  Actions action = null



    def" Login to Model Catalogue"(){


        when:
        loginAdmin()

        then:
        check 'span.mc-name' contains'Model Catalogue'
    }


    def" Create a Data Model"(){

        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'GEO4'
        fill modelCatalogueId with 'MTT-045'

        then:
        check importStep isEnabled()

        when:
        click finishButton

        then:
        check 'div#summary>h4' is 'Data Model GEO4 created'

        when:
        click closeButton

        then:
        check rightSideTitle contains 'GEO4 MTT-045@0.0.1'

    }


    def" Create a Data Class "(){

        when:
        selectInTree 'Data Classes'

        then:
        check rightSideTitle is 'Active Data Classes'


        when:
        click create

        then:
        check modalHeader is 'Data Class Wizard'


        when:
        fill nameLabel with 'CLASS1'

        then:
        check metadataStep isEnabled()

        when:
        click finishButton

        then:
        check 'div.alert' contains 'Data Class CLASS1 created'

        when:
        click exitButton

        then:
        check rightSideTitle  is 'Draft Data Classes'

        and:
        check{infTableCell(1,1)} contains 'CLASS1'
    }


    def"Create a Data Element"(){

        when:
        selectInTree 'Data Elements'

        then:
        check rightSideTitle is 'Active Data Elements'

        when:
        click create

        then:
        check modalHeader is 'Create Data Element'


        when:
        fill nameLabel with 'Element1'
        click save

        then:
        check{infTableCell(1,1)} contains'Element1'

    }


    def"Create Business Rule and add Data Class and Element"(){

        when:
        selectInTree 'Business Rules'

        then:
        check rightSideTitle is 'Active Validation Rules'


        when:
        click create

        then:
        check modalHeader is 'New Validation Rule'


        when:
        fill nameLabel with 'Validation1'
        fill 'input#dataClasses' with 'CLASS1' and pick first item

        then:
        check dataClassConfirmation isDisplayed()

        when:
        fill 'input#dataElements' with 'Element1' and pick first item

        then:
        check dataElementConfirmation isDisplayed()

        when:
        click save

        then:
        check{infTableCell(1,1)} contains 'Validation1'

    }



    def" Verification of Data Class and Element presence"(){

        when:

        click validationRule

        then:
        check rightSideTitle contains 'Validation1'


        expect:
        check dataClassText isDisplayed()
        check dataElementText isDisplayed()

    }



    def"Delete the created Data Model"(){

        when:
        selectInTree 'GEO4'

        then:
        check rightSideTitle contains 'GEO4 MTT-045@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model GEO4?'

        click modalPrimaryButton

    }




}
