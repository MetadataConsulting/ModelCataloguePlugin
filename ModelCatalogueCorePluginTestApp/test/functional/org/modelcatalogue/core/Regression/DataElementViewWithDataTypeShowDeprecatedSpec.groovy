package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.getDelete
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save


@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1270")
class DataElementViewWithDataTypeShowDeprecatedSpec extends AbstractModelCatalogueGebSpec {

    private static final String createButton="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String  importStep="button#step-imports"
    private static final String  finishButton="button#step-finish"
    private static final String  closeButton="div.modal-footer>button:nth-child(2)"
    private static final String  dataElementMenuBar="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String dataTypeLabel ="body > div.modal.fade.ng-isolate-scope.basic-edit-modal-prompt.in > div > div > div.modal-body.ng-scope > form > div:nth-child(4) > label"
    private static final String  deprecatedLink ="a#archive-menu-item-link>span:nth-child(3)"
    private static final String   simpleDataType ="table.pp-table>tbody>tr:nth-child(3)>td>a"
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
        fill nameLabel with 'GEO5'
        fill modelCatalogueId with 'MTT-0456'

        then:
        check importStep isEnabled()

        when:
        click finishButton

        then:
        check 'div#summary>h4' is 'Data Model GEO5 created'

        when:
        click closeButton

        then:
        check rightSideTitle contains 'GEO5 MTT-0456@0.0.1'

    }

    def "Create Data Type"(){

        when:
        selectInTree( "Data Types")

        then:
        check rightSideTitle is 'Active Data Types'


        when:
        click create

        then:
        check 'h4.ng-binding' is 'Create Data Type'

        when:
        fill nameLabel with 'Simple Type'
        fill modelCatalogueId with 'DT -001'
        click save

        then:
        check{infTableCell(1,1)} contains 'Simple Type'
    }

    def"Create Data Element"(){

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
        fill modelCatalogueId with 'DE001'

        then:
        check dataTypeLabel isDisplayed()

        when:

         fill 'input#dataType' with 'Simple Type' and  pick first item
        click save

        then:
        check{infTableCell(1,1)} contains'Element1'

    }

    def"Deprecated Data Element"(){

        when:
        click 'td.col-md-4>span>span>a'

        then:
        check rightSideTitle contains 'Element1 DE001@0.0.1'


        when:
        click dataElementMenuBar
        click deprecatedLink

        then:
        check modalHeader is 'Do you want to mark Data Element Element1 as deprecated?'

        when:
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Element1 DE001@0.0.1'

    }

    def"Verify that Data Element is deprecated"(){

        when:
        selectInTree 'Deprecated Items'

        then:
        check rightSideTitle contains 'Deprecated Catalogue Elements'

        expect:
        check {infTableCell(1,2)} is 'Element1'


        when:
        click 'span.fa-plus-square-o'

        then:
        check simpleDataType is 'Simple Type'

    }

    def"Delete Data Model"(){

        when:
        selectInTree 'GEO5'

        then:
        check rightSideTitle contains 'GEO5 MTT-0456@0.0.1'

        when:
        action = new Actions(driver)
        action.clickAndHold(driver.findElement(By.cssSelector(dataElementMenuBar)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model GEO5?'

        click modalPrimaryButton

    }

}
