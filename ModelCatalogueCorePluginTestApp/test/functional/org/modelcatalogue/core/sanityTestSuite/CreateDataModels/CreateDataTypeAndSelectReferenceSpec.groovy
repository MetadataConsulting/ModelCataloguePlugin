package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@Stepwise
class CreateDataTypeAndSelectReferenceSpec extends AbstractModelCatalogueGebSpec {
    private static final String reference= "input#pickReferenceType"
    private static final String   alert ="body > div.modal.fade.ng-isolate-scope.basic-edit-modal-prompt.in > div > div > div.modal-body.ng-scope > form > div.in.collapse > div > label"
    private static final String table ="tr.inf-table-item-row>td:nth-child(1)"
    private static final String createButton="a#role_data-models_create-data-modelBtn"
    private static final String finishStep ="button#step-finish"
    private static final String closeStep ="div.modal-footer>button:nth-child(2)"
    private static final String finishButton ="button#step-finish"
    private static final String wizardSummary = 'td.col-md-4'
    private static final String exitButton = "#exit-wizard"
    private static final String menuBar = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static Actions actions=null




    def"login to Model Catalogue and create Data Model"() {
        when:
        loginCurator()

        then:
        check createButton displayed


        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'


        when:
        fill nameLabel with 'Reference'
        fill modelCatalogueId with 'METT-001'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(3000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Reference created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Reference METT-001@0.0.1'
    }

    def"create a Data Class"(){

        when:

        selectInTree 'Data Classes'

        then:
        check rightSideTitle is 'Active Data Classes'

        when:
        click create
        then:
        check modalHeader contains 'Data Class Wizard'

        when:
        fill nameLabel with "Class 1"

        fill modelCatalogueId with "MET-2233"

        fill description with "my description of data type${System.currentTimeMillis()}"



        and: 'click green button'
        click finishButton
        Thread.sleep(2000L)

        then:
        check wizardSummary contains "Class 1"
        Thread.sleep(3000L)

        cleanup:
        click exitButton



    }

    def"Select Data Types"(){


        when:
              selectInTree 'Data Types'

        then:
        check rightSideTitle contains 'Active Data Types'



    }
    def"Navigate to data type page"() {
        when:
             click create
        then:
             check modalHeader contains 'Create Data Type'
    }




    def " fill the create data type form"(){


        when:
        fill nameLabel with "TESTING_DATA_TYPE"

        fill modelCatalogueId with "MET-333"


        and: 'select references button and save'
        click reference

        then:
        check alert contains 'Data Class'

        when:
        Thread.sleep(3000)
        fill 'input#dataClass' with'Class 1'
        remove messages



        and:
        click save

        then:
        check { infTableCell(1, 1) } contains "TESTING_DATA_TYPE"

    }
    def"delete the created Data Model"(){

        when:
        selectInTree 'Reference'

        then:
        check rightSideTitle contains 'Reference METT-001@0.0.1'


        when:
        actions = new Actions(driver)
        actions.clickAndHold(driver.findElement(By.cssSelector(menuBar)))

        and:
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Reference?'

        and:'confirmation'
        click modalPrimaryButton

        then:
        check table gone
    }
}
