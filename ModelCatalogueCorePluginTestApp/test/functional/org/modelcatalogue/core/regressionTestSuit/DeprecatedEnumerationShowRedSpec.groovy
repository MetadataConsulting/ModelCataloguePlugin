package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueContent
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.gebUtils.Common.delete
import static org.modelcatalogue.core.gebUtils.Common.getCreate
import static org.modelcatalogue.core.gebUtils.Common.getDescription
import static org.modelcatalogue.core.gebUtils.Common.getMessages
import static org.modelcatalogue.core.gebUtils.Common.getModalDialog
import static org.modelcatalogue.core.gebUtils.Common.getModelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.getNameLabel
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save

@Stepwise @Title("https://metadataStep.atlassian.net/browse/MET-1245?filter=11400")

class DeprecatedEnumerationShowRedSpec extends AbstractModelCatalogueGebSpec {

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String  dataModel= "ul.catalogue-element-treeview-list-root>li>div>span>span"
    private static final String saveAndCreateAnother = "a#role_modal_modal-save-and-add-anotherBtn"
    private static final String  enumeration = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String enumeratedType = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String deprecated = "a#archive-menu-item-link>span:nth-child(3)"
    private static final String dataModelMenuBar = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    public static final String pickEnumeratedType = 'input#pickEnumeratedType'
    public static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
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
        fill nameLabel with 'Deprecated Data Model'
        fill modelCatalogueId with 'MET-786'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Deprecated Data Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Deprecated Data Model MET-786@0.0.1'
    }

    def"create enumerated data types"(){

        when:
        selectInTree 'Data Types'
        remove messages
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
        Thread.sleep(3000l)
        fill nameLabel with 'Enumeration 2'
        click pickEnumeratedType
        fillMetadata '01': 'cat', '02': 'dog', '03': 'mouse', '04': 'tiger'

        click save

        then:
        check { infTableCell(1, 1) } contains "Enumeration 2"
        check { infTableCell(1, 2) } contains "01: cat\n" + "02: dog\n" + "03: mouse\n" + "04: tiger"

        and:
        check { infTableCell(2, 1) } contains "Enumeration 1"
        check { infTableCell(2, 2) } contains "01: one\n" + "02: two\n" + "03: three\n" + "04: four\n" + "05: five"

    }

    def"deprecated enumeration 1 and enumeration2"(){

        when:
        click enumeration
        click enumeratedType
        click deprecated

        then:
        check modalHeader is 'Do you want to mark Enumerated Type Enumeration 2 as deprecated?'
        and:
        click modalPrimaryButton


        when:
        selectInTree 'Data Types'
        Thread.sleep(2000l)
        click enumeration

        then:
        check rightSideTitle contains 'Enumeration'

        when:
        click enumeratedType
        click deprecated

        then:
        check modalHeader is 'Do you want to mark Enumerated Type Enumeration 1 as deprecated?'
        and:
        click modalPrimaryButton


    }

    def"verify that data types are deprecated"(){

        when:
        selectInTree 'Deprecated Items'

        then:
        check rightSideTitle contains 'Deprecated Catalogue Elements'


        expect:
        check { infTableCell(1, 2) } contains "Enumeration 1"
        check { infTableCell(2, 2) } contains "Enumeration 2"

    }

    def"delete the created data model"(){

        when:
        click dataModel

        then:
        check rightSideTitle  contains 'Deprecated Data Model MET-786@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Deprecated Data Model?'
        and:
        click modalPrimaryButton


    }
}
