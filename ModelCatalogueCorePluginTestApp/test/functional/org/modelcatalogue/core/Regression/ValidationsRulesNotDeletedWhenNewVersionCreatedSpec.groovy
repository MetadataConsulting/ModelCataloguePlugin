package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.finalize
import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.rightSideTitle


@Stepwise @Title("https://metadataStep.atlassian.net/browse/MET-1344?filter=11400")
class ValidationsRulesNotDeletedWhenNewVersionCreatedSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String  createNewVersionButton = "a#role_modal_modal-create-new-versionBtn"
    private static final String importStep = "button#step-imports"
    private static final String  dataModel = "ul.catalogue-element-treeview-list-root>li>div>span>span"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String BusinessRules = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(5)>div>span>span"
    private static final String modelCatalogue= "span.mc-name"
    private static final String  dataModelMenuBar= "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  semanticVersion= "input#semanticVersion"
    private static final String  revisionNotes= "textarea#revisionNotes"
    private static final String  finalizedButton= "a#role_modal_modal-finalize-data-modalBtn"
    private static final String   createNewVersion= "a#create-new-version-menu-item-link>span:nth-child(3)"
    private static final String   draftModel= "tr.warning>td:nth-child(2)>a"
    private static final String   versions= "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(10)>div>span>span"
    private static  Actions action = null


    def"login to model catalogue and create data model"(){

        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'Validation Rule Model'
        fill modelCatalogueId with 'MED-2212'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click importStep

        then:
        check 'div.form-group>label' is 'Import Existing Data Models'

        when:
        fill 'input#name' with 'MET-523' and pick first item

        then:
        check 'span.with-pointer' isDisplayed()

        when:
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Validation Rule Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Validation Rule Model MED-2212@0.0.1'
    }

    def "create a business rule"(){

        when:
        click BusinessRules
        click create

        then:
        check modalDialog displayed

        when:
        fill 'name' with 'Validation Rule'

        click save

        then:
        check { infTableCell(1, 1) } contains 'Validation Rule'

    }

    def "finalize the created data model"(){

        when:
        click modelCatalogue
        select 'Validation Rule Model'

        then:
        Thread.sleep(4000l)
        check rightSideTitle contains 'Validation Rule Model MED-2212@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click finalize

        then:
        check modalHeader is 'Finalize Data Model'

        when:
        fill semanticVersion with '1.0.0'
        fill revisionNotes with 'TESTING VALIDATION RULES'
        click finalizedButton
        Thread.sleep(3000l)
        click modalPrimaryButton


        then:
        check rightSideTitle contains 'Validation Rule Model MED-2212@1.0.0'
        and:
        check { infTableCell(1, 4) } contains 'Validation Rule Model (1.0.0) finalized'

    }

    def" create a new version of the finalized data model"(){

        when:
        click dataModelMenuBar
        click createNewVersion

        then:
        check modalHeader is 'New Version of Data Model'

        when:
        fill semanticVersion with '2.0.0'
        click createNewVersionButton
        Thread.sleep(3000L)
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Validation Rule Model MED-2212@1.0.0'


        when:
        click versions

        then:
        check rightSideTitle contains 'Validation Rule Model History'

        expect:
        check { infTableCell(1, 1) } contains '2.0.0'


    }

    def"verify that the validation rule is not deleted when a new version is created"(){

        when:
        click draftModel

        then:
        check rightSideTitle contains 'Validation Rule Model MED-2212@2.0.0'

        when:
        click BusinessRules

        then:
        check { infTableCell(1, 1) } contains 'Validation Rule'

    }

    def"delete the draft data model"(){

        when:
        click dataModel

        then:
        check rightSideTitle contains 'Validation Rule Model MED-2212@2.0.0'


        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))

        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Validation Rule Model?'

        and:
        click modalPrimaryButton


    }
}
