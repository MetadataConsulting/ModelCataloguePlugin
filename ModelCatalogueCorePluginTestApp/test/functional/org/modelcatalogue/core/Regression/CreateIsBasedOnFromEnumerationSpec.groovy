package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueContent
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getBackdrop
import static org.modelcatalogue.core.geb.Common.getMessages
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
class CreateIsBasedOnFromEnumerationSpec extends  AbstractModelCatalogueGebSpec {


    private static final String createButton ="a#role_data-models_create-data-modelBtn"
    private static final String baseEnumeration = '#baseEnumeration'
    private static final String pickSubset = '#pickSubsetType'
    private static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    private static final String pickEnumeratedType = '#pickEnumeratedType'
    private static final String finishStep ="button#step-finish"
    private static final String modalDialog = "div.modal"
    private static final String closeStep ="div.modal-footer>button:nth-child(2)"
    private static final String dataType ="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String  activeUsers ="div.form-group>table>tbody>tr:nth-child(1)>th"
    private static final String isBasedOnEnumeration ="#isBasedOn-changes > div.inf-table-body > table > tbody > tr > td:nth-child(2) > a"
    private static final String dataModelMenuBar ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String create ="span.fa-plus-circle"
    private static final String saveAndCreateAnother ="a#role_modal_modal-save-and-add-anotherBtn>span:nth-child(2)"
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
        fill nameLabel with 'Data Model1'
        fill modelCatalogueId with 'M-001'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(3000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Data Model1 created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Data Model1 M-001@0.0.1'

        when:
        fastAction"Activity"

        then:
        check modalHeader is 'Recent Activity'

        and:
        check activeUsers is 'Most Recent Active Users'
        click modalPrimaryButton
    }

    def"create a simple type"(){

        when:
        selectInTree 'Data Types'
        click create

        then:
        check 'h4.ng-binding' is 'Create Data Type'


        when:
        fill nameLabel with 'SIMPLE TYPE'
        fill modelCatalogueId with 'm-001'
        fill description with 'TESTING '
        click save

        then:
        check { infTableCell(1, 1) } contains "SIMPLE TYPE"

    }

    def"create an enumeration type"(){

        when:
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Enumeration 1'

        click pickEnumeratedType


        check enumerationsTableEditor displayed

        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'

        click saveAndCreateAnother

        then:
        check 'h4.ng-binding' is 'Create Data Type'

        when:
        Thread.sleep(2000l)
        fill nameLabel with 'Enumeration 2'

        click pickEnumeratedType


        check enumerationsTableEditor displayed

        fillMetadata '06': 'six', '07': 'seven', '08': 'eight', '10': 'ten'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 2"
    }

    def"create a subset type"(){

        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        Thread.sleep(2000)
        fill nameLabel with 'Subset'

        click pickSubset

        check baseEnumeration displayed

        fill baseEnumeration with 'Enumeration 1' and pick first item

        click '#subtype-enum-1'
        click '#subtype-enum-2'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Subset"
    }

    def"check that subset is based on enumeration"(){


        when:
        click dataType

        then:
        check rightSideTitle contains 'Subset'


        expect:
        check { infTableCell(1, 1) } contains "Enumeration 1"

        selectTab 'isBasedOn'
        check isBasedOnEnumeration contains "Enumeration 1 (Data Model1 0.0.1)"

    }

    def"create a relationship is Bases on"(){

        when:
        selectInTree 'Data Types'
        click dataType
        selectTab 'isBasedOn'
        click create

        then:
        check 'h4.ng-binding' is 'Enumeration 1'

        when:
        fill 'input#element' with'Enumeration 2' and pick first item
        click modalPrimaryButton

        then:
        check isBasedOnEnumeration contains 'Enumeration 2 (Data Model1 0.0.1)'

    }

    def"delete the data model"(){

        when:
        selectInTree 'Data Model1'

        then:
        check rightSideTitle contains 'Data Model1 M-001@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Data Model1?'

        and:
        click modalPrimaryButton
    }



}
