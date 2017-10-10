package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueContent
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getBackdrop
import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getMessages
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.modalDialog
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
class CreateDataTypeAndSelectSubsetSpec extends AbstractModelCatalogueGebSpec {

    private static final String  subset= "input#pickSubsetType"
    private static final String enumeratedTypeBase ="input#baseEnumeration"
    private static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    private static final String pickEnumeratedType = '#pickEnumeratedType'
    private static final String  Rule = 'form.ng-valid>div:nth-child(4)>label'
    private static final String  regularExpression = 'p.help-block>a:nth-child(5)'
    private static final String  dataModelMenuBar = 'a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static  Actions action = null



    def " login and navigate to Data model"() {

        when:
        loginCurator()
        select 'Test 3'
        selectInTree 'Data Types'

        then:
        check rightSideTitle contains 'Active Data Types'
    }

    def "Create a Simple Type"() {
        when:
        click create
        then:
        check modalHeader contains 'Create Data Type'

        when:
        fill nameLabel with 'Simple Type'
        fill modelCatalogueId with 'MET-234'
        fill description with 'TESTING INFO'

        and:
        click save

        then:
        check modalDialog gone
        and:
        check backdrop gone
    }

    def " create Enumerated type"() {
        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Enumeration 1'

        click pickEnumeratedType


        check enumerationsTableEditor displayed

        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 1"


    }
     def"create a subset type"(){
         when:
         remove messages
         click create

         then:
         check modalDialog displayed

         when:
         fill nameLabel with 'Enumeration 2'

         click subset

         check enumeratedTypeBase displayed

         fill enumeratedTypeBase with 'Enumeration 1' and pick first item

         click '#subtype-enum-1'
         click '#subtype-enum-2'

         click save

         then:
         check modalDialog gone
         check backdrop gone
         check { infTableCell(1, 1) } contains "Enumeration 2"

     }

    def "create a second subset type"(){

        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Enumeration 3'

        click subset
        click Rule
        click regularExpression

        check enumeratedTypeBase displayed

        fill enumeratedTypeBase with 'Enumeration 1' and pick first item

        click '#subtype-enum-1'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 3"
        check { infTableCell(1, 2) } contains '01: one'

        when:
        click 'span.mc-name'
        select 'Test 3' open'Data Types' select'Enumeration 3'

        then:
        check rightSideTitle contains 'Enumeration 3'


        expect:
        check { infTableCell(1, 1) } contains "Enumeration 1"

    }

    def"delete the created enumeration"(){

        when:
        action = new Actions(driver)
        action = action.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Enumerated Type Enumeration 3?'

        and:
        click modalPrimaryButton


    }

}
