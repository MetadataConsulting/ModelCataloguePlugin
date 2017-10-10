package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise
import spock.lang.Title
import spock.lang.Unroll

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getInlineEdit
import static org.modelcatalogue.core.geb.Common.getInlineEditSubmit
import static org.modelcatalogue.core.geb.Common.getUp
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle


@Stepwise @Title("https://metadataStep.atlassian.net/browse/MET-1025?filter=11400")
class CreateDataClassWithMetadataValueBlankSpec extends AbstractModelCatalogueGebSpec {

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String finishStep = "#step-finish > span"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String  exitButton = "button#exit-wizard"
    private static final String   createdDataClass = "td.col-md-4>span>span>a"
    private static final String   dataModelMenuBarButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    static final String  metadata = 'form.ng-pristine>div:nth-child(7)>p>span>span:nth-child(1)'
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
        fill nameLabel with 'Data Class Model'
        fill modelCatalogueId with 'ME-2ww@!!!'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Data Class Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Data Class Model ME-2ww@!!!@0.0.1'

    }

    def"create data class with metadata value blank"(){

        when:
        selectInTree 'Data Classes'
        click create

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'DATA CLASS1'
        fill modelCatalogueId with 'MD-001'
        fill description with 'METADATA VALUE BLANK'
        Thread.sleep(3000L)
        $("button#step-metadata").click()

        then:
        check 'div#metadata>form>div>h4' is 'Metadata'

        when:
        fillMetadata '01': '', '02': '', '03': '', '04': ''
        click finishStep


        then:
        check 'div.alert' contains 'Data Class DATA CLASS1 created'

        when:
        click exitButton

        then:
        check rightSideTitle contains 'Draft Data'
    }
     @Unroll
    def"verify that metadata is created"(int location , String key){

        when:
        selectInTree 'Data Classes'
        click createdDataClass

        then:
        check rightSideTitle contains 'DATA CLASS1 MD-001@0.0.1'


        click metadata
        Thread.sleep(3000L)

        expect:
        $("table.table-responsive>tbody>tr:nth-child($location)>td:nth-child(1)").text().contains(key)

        where:
        location || key
        1        || '01'
        2        || '02'
        3        || '03'


    }
    def"edit and add value to metadata"(){

        expect:
        click inlineEdit
        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four'
        3.times { scroll up }
        click inlineEditSubmit
        Thread.sleep(2000l)
        browser.driver.navigate().refresh()



    }
    def"verify that keys and values are displayed"() {

         click metadata

         expect:
         check 'table.table-responsive>tbody>tr:nth-child(1)>td:nth-child(1)' contains("01")
         check 'table.table-responsive>tbody>tr:nth-child(2)>td:nth-child(1)' contains("02")
         check 'table.table-responsive>tbody>tr:nth-child(3)>td:nth-child(1)' contains("03")

         and:
         check 'table.table-responsive>tbody>tr:nth-child(1)>td:nth-child(2)'  contains 'one'
         check 'table.table-responsive>tbody>tr:nth-child(2)>td:nth-child(2)'  contains 'two'
         check 'table.table-responsive>tbody>tr:nth-child(3)>td:nth-child(2)'  contains 'three'


    }

    def"delete the created data model"(){


        when:
        selectInTree 'Data Class Model'

        then:
        check rightSideTitle contains 'Data Class Model ME-2ww@!!!@0.0.1'

        when:
        actions = new Actions(driver)
        actions.clickAndHold(driver.findElement(By.cssSelector(dataModelMenuBarButton)))
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model Data Class Model?'
        and:
        click modalPrimaryButton

    }

}
