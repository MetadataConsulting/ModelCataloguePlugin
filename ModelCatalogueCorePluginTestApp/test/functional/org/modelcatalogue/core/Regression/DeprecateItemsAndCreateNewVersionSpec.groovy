package org.modelcatalogue.core.Regression


import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueContent
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getInlineEdit
import static org.modelcatalogue.core.geb.Common.getInlineEditSubmit
import static org.modelcatalogue.core.geb.Common.getUp
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save


@Stepwise @Title("https://metadata.atlassian.net/browse/MET-841?filter=11400")
class DeprecateItemsAndCreateNewVersionSpec extends  AbstractModelCatalogueGebSpec{


    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String createAnother = "div.modal-footer>button:nth-child(1)"
    private static final String  exitButton = "button#exit-wizard"
    private static final String  saveAndAddAnother = "a#role_modal_modal-save-and-add-anotherBtn"
    private static final String  symbol = "input#symbol"
    private static final String  primitiveType = "input#pickPrimitiveType"
    private static final String  measurementUnit = "input#measurementUnit"
    private static final String   enumerationType = "input#pickEnumeratedType"
    private static final String   createDataClass= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String   dataModelMenuBar= "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String    createRelationship= "a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String     isBasedOn= "ul.nav-tabs>li:nth-child(3)>a>span:nth-child(1)"
    private static final String     Versions= "a#create-new-version-menu-item-link>span:nth-child(3)"
    private static final String deprecatedAndRestore = "a#archive-menu-item-link>span:nth-child(3)"
    private static final String  createVersions = "a#role_modal_modal-create-new-versionBtn"
    private static final String  draftModel= "tr.warning>td:nth-child(2)>a"
    static final String  metadata = 'form.ng-pristine>div:nth-child(7)>p>span>span:nth-child(1)'
    public static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    private static Actions actions=null



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
        fill nameLabel with 'Restore Version Model'
        fill modelCatalogueId with 'TRE-B34'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Restore Version Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Restore Version Model TRE-B34@0.0.1'
    }

    def"add some data classes"(){

        when:
        selectInTree 'Data Classes'
        click create

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'DATA CLASS1'
        fill modelCatalogueId with 'BK -001'
        fill description with 'TESTING DEPRECATED MODEL AND CREATE NEW VERSION'
        click finishStep
        then:
        check 'div.alert' contains 'Data Class DATA CLASS1 created'

        when:
        click createAnother

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'DATA CLASS2'
        fill modelCatalogueId with 'BK -002'
        fill description with 'TESTING DEPRECATED MODEL AND CREATE NEW VERSION'
        click finishStep
        then:
        check 'div.alert' contains 'Data Class DATA CLASS2 created'

        when:
        click exitButton

        then:
        check rightSideTitle contains 'Draft Data'

    }

    def"add some  Measurement units"(){

        selectInTree 'Measurement Units'
        click create

        expect:
        check modalHeader is 'Create Measurement Unit'

        when:
        fill nameLabel with 'KILOGRAM'
        fill symbol with 'KG'
        fill modelCatalogueId with 'KE -001'
        fill description with 'TESTING DEPRECATED MODEL AND CREATE NEW VERSION'
        click saveAndAddAnother

        then:
        check modalHeader is 'Create Measurement Unit'

        when:
        Thread.sleep(2000L)
        fill nameLabel with 'DELTA'
        fill symbol with 'DT'
        fill modelCatalogueId with 'KE -002'
        fill description with 'TESTING DEPRECATED MODEL AND CREATE NEW VERSION'
        Thread.sleep(2000L)
        click save

        then:
        check { infTableCell(1, 2) } contains "DELTA"
        check { infTableCell(2, 2) } contains "KILOGRAM"


    }
    def"add some data types "(){

        selectInTree 'Data Types'
        Thread.sleep(3000L)
        click create

        expect:
        check 'h4.ng-binding' is 'Create Data Type'

        when:
        Thread.sleep(2000L)
        fill nameLabel with 'PRIMITIVE TYPE'
        fill modelCatalogueId with 'BD -001'
        fill description with 'TESTING DEPRECATED MODEL AND CREATE NEW VERSION'
        click primitiveType

        then:
        check measurementUnit displayed

        when:
        fill measurementUnit with 'KILOGRAM'and pick first item
        click saveAndAddAnother

        then:
        check 'h4.ng-binding' is 'Create Data Type'

        when:
        Thread.sleep(2000L)
        fill nameLabel with 'ENUMERATION TYPE'
        fill modelCatalogueId with 'BD -002'
        fill description with 'TESTING DEPRECATED MODEL AND CREATE NEW VERSION'
        click enumerationType

        then:
        check enumerationsTableEditor displayed

        when:
        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'
        click save

        then:
        check { infTableCell(1, 1) } contains "ENUMERATION TYPE"
        check { infTableCell(2, 1) } contains "PRIMITIVE TYPE"
    }

    def" create Is bases on relationship"(){

        when:
        selectInTree  'Data Classes'
        click createDataClass
        click dataModelMenuBar
        click createRelationship

        then:
        check 'h4.ng-binding' is 'DATA CLASS1'

        when:
        fill 'select#type' with 'is based on'
        fill 'input#element' with 'DATA CLASS2' and pick first item
        click modalPrimaryButton
        Thread.sleep(3000L)
        click isBasedOn


        then:
        check { infTableCell(1, 2) } contains "DATA CLASS2 (Restore Version Model 0.0.1)"


    }

    def"deprecated the created data model"(){

        when:
        selectInTree 'Restore Version Model'
        Thread.sleep(3000l)
        click dataModelMenuBar
        click deprecatedAndRestore

        then:
        check modalHeader  contains 'Do you want to mark Data Model Restore Version Model as deprecated?'
        and:
        click modalPrimaryButton
        Thread.sleep(3000L)

    }

    def" restored the deprecated data model"(){

        when:
        click dataModelMenuBar
        click deprecatedAndRestore

        then:
        check modalHeader is 'Do you want to restore Data Model Restore Version Model as finalized?'
        and:
        click modalPrimaryButton

    }
    def"verify that can edit the data class is based on relationship"(){

        when:
        selectInTree 'Data Classes'
        Thread.sleep(2000l)
        click createDataClass
        then:
        check rightSideTitle contains 'DATA CLASS1 BK -001@0.0.1'

        when:
        click metadata
        Thread.sleep(3000L)
        click inlineEdit
        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four'
        3.times { scroll up }
        click inlineEditSubmit
        browser.driver.navigate().refresh()
        Thread.sleep(2000l)
        click metadata


        then:
        check 'table.table-responsive>tbody>tr:nth-child(1)>td:nth-child(1)' contains("01")
        check 'table.table-responsive>tbody>tr:nth-child(1)>td:nth-child(2)'  contains 'one'
    }

    def"create a new version of the data model"(){

        when:
        selectInTree('Restore Version Model')
        Thread.sleep(3000L)
        click dataModelMenuBar
        click Versions

        then:
        check modalHeader is 'New Version of Data Model'

        when:
        fill 'input#semanticVersion' with '2.0.0'
        click createVersions
        Thread.sleep(3000l)
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Restore Version Model TRE-B34@0.0.1'

        when:
        selectInTree('Versions')

        then:
        check { infTableCell(1, 1) } contains "2.0.0"

    }
    def " check that  the deprecated elements are displayed in deprecated Items"(){

        when:
        selectInTree('Deprecated Items')

        then:
        check { infTableCell(1, 2) } contains "DATA CLASS1"
        check { infTableCell(2, 2) } contains "DATA CLASS2"
        check { infTableCell(3, 2) } contains "DELTA"
        check { infTableCell(4, 2) } contains "ENUMERATION TYPE"
        check { infTableCell(5, 2) } contains "KILOGRAM"
        check { infTableCell(6, 2) } contains "PRIMITIVE TYPE"

    }

    def"delete the draft data model"(){

        when:
        selectInTree 'Versions'

        then:
        check { infTableCell(1, 1) } contains "2.0.0"
        check { infTableCell(2, 1) } contains "0.0.1"

        when:
        click draftModel

        then:
        check rightSideTitle contains 'Restore Version Model TRE-B34@2.0.0'

        when:
        actions = new Actions(driver)
        actions.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Restore Version Model?'
        and:
        click modalPrimaryButton

    }


}
