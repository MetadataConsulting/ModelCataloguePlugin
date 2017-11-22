package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueContent
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title
import static org.modelcatalogue.core.gebUtils.Common.create
import static org.modelcatalogue.core.gebUtils.Common.delete
import static org.modelcatalogue.core.gebUtils.Common.description
import static org.modelcatalogue.core.gebUtils.Common.getBackdrop
import static org.modelcatalogue.core.gebUtils.Common.getModalDialog
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.modelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.nameLabel
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save


@Stepwise @Title("https://metadataStep.atlassian.net/browse/MET-1169?filter=11400")
class AfterEditEnumerationRightSidePanelUpdateSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String  firstEnu= "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>ul>li>ul>li:nth-child(1)>div>span>span"
    private static final String  dataModel= "ul.catalogue-element-treeview-list-root>li>div>span>span"
    private static final String  Enu1 = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>ul>li>div>span>span"
    private static final String pickEnumeratedType = "input#pickEnumeratedType"
    private static final String dataTypes = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>div>span>span"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String  updateButton = "button.update-object"
    private static final String plusButton = "tbody.ng-isolate-scope>tr:nth-child(3)>td:nth-child(4)>p>a:nth-child(1)>span"
    private static final String dataMenuBarButton ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    private static  Actions action = null



    def"login to model catalogue and create a data model"(){

        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'Enumeration Data Model'
        fill modelCatalogueId with 'MAS -12'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Enumeration Data Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Enumeration Data Model MAS -12@0.0.1'

    }

    def"create a enumeration type "(){

        when:
        click dataTypes
        click create

        then:
        check 'h4.ng-binding' is 'Create Data Type'


        when:
        fill nameLabel with 'Enu1'
        fill modelCatalogueId with 'MAS-34'
        fill description with 'TESTING ENUMERATION'
        click pickEnumeratedType

        then:
        check enumerationsTableEditor displayed

        when:
        fillMetadata '01': 'one', '02': 'two', '03': 'three'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enu1"

    }

    def"edit the created data type"(){

        when:
        click dataTypes
        click dataTypes
        click Enu1
        Thread.sleep(3000L)
        click firstEnu


        then:
        check rightSideTitle contains 'Enu1 Enumerations'


        when:
        click plusButton
        Thread.sleep(2000l)
        fillMetadata('01': 'one', '02': 'two', '03': 'three', '04': 'four')
        Thread.sleep(3000L)
        click updateButton

        then:
        check rightSideTitle contains 'Enu1 Enumerations'

    }

    def"verify that enumeration type is updated"(){

        when:
        click 'span.mc-name'
        Thread.sleep(2000L)
        select 'Enumeration Data Model'
        selectInTree 'Data Types'


        then:
        check { infTableCell(1, 1) } contains "Enu1"
        check { infTableCell(1, 2) } contains "01: one\n" + "02: two\n" + "03: three\n" + "04: four"
    }

    def"delete the created data model"(){

        when:
        click dataModel

        then:
        check rightSideTitle contains 'Enumeration Data Model MAS -12@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataMenuBarButton)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Enumeration Data Model?'
        and:
        click modalPrimaryButton
    }

}
