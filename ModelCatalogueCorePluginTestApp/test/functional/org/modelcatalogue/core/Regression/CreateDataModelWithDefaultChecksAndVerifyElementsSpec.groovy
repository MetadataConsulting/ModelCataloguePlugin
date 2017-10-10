package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.createNewDataModel
import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save


@Stepwise
class CreateDataModelWithDefaultChecksAndVerifyElementsSpec extends  AbstractModelCatalogueGebSpec{



    private static final String  policy= "form.ng-pristine>div:nth-child(4)>div>ng-include>div>div>span>a"
    private static final String exit_wizard = "button#exit-wizard>span:nth-child(1)"
    private static final String  cancelButton = "a#role_modal_modal-cancelBtn"
    private static final String  dataModelMenuBar = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static  Actions action = null


    def"login to model catalogue and create data model"(){

        when:
        loginAdmin()

        then:
        check createNewDataModel isDisplayed()

        when:
        click createNewDataModel

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'Default Checks Model'
        fill modelCatalogueId with 'DC-23'
        click 'span.glyphicon-remove'

        then:
        check 'span.glyphicon-remove'isGone()

        when:
        fill 'input#dataModelPolicy'with 'Default Checks' and pick first item
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check closeStep isDisplayed()

        when:
        click exit_wizard
        click modalPrimaryButton

        then:
        check createNewDataModel isDisplayed()


        when:
        click 'span.mc-name'
        Thread.sleep(4000L)

        select 'Default Checks Model'

        then:
        check rightSideTitle contains 'Default Checks Model DC-23@0.0.1'

        and:
        check policy is 'Default Checks'
    }

    def"verify that you can create a data element without data type"(){


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
        fill modelCatalogueId with 'DC-11'
        fill description with 'TESTING'
        click save

        then:
        check 'div.alert' contains 'Data type is missing for Element1 [null@0.0.1] (DRAFT:DataElement:null)'
    }

    def"create a simple data type"(){

        when:
        click cancelButton
        selectInTree 'Data Types'

        then:
        check rightSideTitle is 'Active Data Types'

        when:
        click create

        then:
        check 'h4.ng-binding' is 'Create Data Type'

        when:
        fill nameLabel with 'SIMPLE TYPE'
        fill modelCatalogueId with 'DC003'
        fill description with 'TESTING DEFAULT CHECKS'
        click save

        then:
        check 'div.alert-success' contains 'Name of SIMPLE TYPE [null@0.0.1] (DRAFT:DataType:null) contains illegal characters ("_", "-" or " ")'

        when:
        click cancelButton
        selectInTree 'Data Types'
        click create

        fill nameLabel with 'simpleType'
        fill modelCatalogueId with 'DC003'
        fill description with 'TESTING DEFAULT CHECKS'
        click save

        then:
        check { infTableCell(1, 1) } contains 'simpleType'


    }

    def"create data element with data type"(){

        when:
        selectInTree 'Data Elements'
        click create

        fill nameLabel with 'DATA ELEMENT'
        fill modelCatalogueId with 'DC004'
        fill description with 'TESTING DEFAULT CHECKS'
        click save

        then:
        check 'div.alert' contains 'Data type is missing for DATA ELEMENT [null@0.0.1] (DRAFT:DataElement:null)'


        when:
        click cancelButton

        selectInTree 'Data Elements'
        click create

        fill nameLabel with 'dataElement'
        fill modelCatalogueId with 'DC004'
        fill description with 'TESTING DEFAULT CHECKS'
        fill 'input#dataType'with 'simpleType' and pick first item
        click save

        then:
        check { infTableCell(1, 1) } contains 'dataElement'

    }
    def"delete the created data model"(){

        when:
        selectInTree 'Default Checks Model'

        then:
        check rightSideTitle contains 'Default Checks Model DC-23@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Default Checks Model?'

        and:
        click modalPrimaryButton
    }
}
