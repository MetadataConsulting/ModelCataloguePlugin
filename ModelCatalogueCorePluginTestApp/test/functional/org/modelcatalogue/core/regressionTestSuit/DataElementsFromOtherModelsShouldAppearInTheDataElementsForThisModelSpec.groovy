package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.gebUtils.Common.description
import static org.modelcatalogue.core.gebUtils.Common.item
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.modelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.nameLabel
import static org.modelcatalogue.core.gebUtils.Common.pick
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save


@Stepwise @Title("https://metadataStep.atlassian.net/browse/MET-1524")
class DataElementsFromOtherModelsShouldAppearInTheDataElementsForThisModelSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton ="a#role_data-models_create-data-modelBtn"
    private static final String deleteButton ="a#delete-menu-item-link>span:nth-child(3)"
    private static final String importStep ="button#step-imports"
    private static final String  exitButton ="button#exit-wizard"
    private static final String  alert ="div.alert>div>span"
    private static final String finishStep ="button#step-finish"
    private static final String  element ="input#data-element"
    private static final String label ="div.form-group>label"
    private static final String elementStep ="button#step-elements"
    private static final String closeStep ="div.modal-footer>button:nth-child(2)"
    private static final String dataMenuBarButton ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  createNewDataElement ="a#catalogue-element-create-dataElement-menu-item-link>span:nth-child(3)"
    private static final String  createDataTypes ="a#catalogue-element-create-dataType-menu-item-link>span:nth-child(3)"
    private static final String  createDataClass ="a#catalogue-element-create-dataClass-menu-item-link>span:nth-child(3)"
    private static final String  newDataClass ="td.col-md-4>span>span>a"
    private static final String  importedDataElementFromTreeView="ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(1)>ul>li>ul>li>div>span>span"


    def"login to model and create a data model"() {

        when:
        loginAdmin()

        then:
        check createButton isDisplayed()


        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'


        when:
        fill nameLabel with 'Data Element From'
        fill modelCatalogueId with 'MET-2299'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(3000L)
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
        check 'div#summary>h4' is 'Data Model Data Element From created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Data Element From MET-2299@0.0.1'


    }
    def" create a data element"(){
        when:
        click dataMenuBarButton
        click createNewDataElement

        then:
        check modalHeader is 'Create Data Element'


        when:
        fill nameLabel with 'Element1'
        fill modelCatalogueId with 'MET 11'
        fill description with 'TESTING DATA ELEMENT'
        click save

        then:
        check rightSideTitle contains 'Draft Data'


    }

    def" create a simple data type"(){

        when:

        selectInTree  'Data Element From'

        then:
        check rightSideTitle  contains 'Data Element From MET-2299@0.0.'


        when:
        click dataMenuBarButton
        click createDataTypes

        then:
        check modalHeader is 'Create Data Type'

        when:
        fill nameLabel with 'SIMPLE TYPE 1'
        fill modelCatalogueId with 'MER-12'
        fill description with 'DATA TYPE FOR TESTING'
        click save

        then:
        check rightSideTitle contains 'Draft Data'

    }

  def" create a data class and add data element from imported data model"(){

      when:

      selectInTree  'Data Element From'

      then:
      check rightSideTitle contains 'Data Element From MET-2299@0.0.1'

      when:
      click dataMenuBarButton
      click createDataClass

      then:
      check modalHeader is 'Data Class Wizard'

      when:
      fill nameLabel with 'DATA CLASS 1'
      fill modelCatalogueId with 'MD-34'
      fill description with 'TESTING DATA CLASS'
      click elementStep

      then:
      check label is 'Data Element'

      when:
      Thread.sleep(2000L)
      fill element with 'MET-523.M1.DE1 ' and pick first item

      then:
      check 'span.with-pointer' isDisplayed()

      when:
      click finishStep

      then:
      check alert is 'Data Class DATA CLASS 1 created'

      when:
      click exitButton

      then:
      check  rightSideTitle contains 'Draft Data'



  }

    def" verify that the new data class contains data element from imported data model"(){

        when:
        selectInTree  'Data Classes'
        click newDataClass

        then:
        check rightSideTitle contains 'DATA CLASS 1 MD-34@0.0.1'


        expect:
        check { infTableCell(1, 1) } contains 'MET-523.M1.DE1'
        check { infTableCell(1, 3) } contains 'MET-523.M1.VD1'


        when:
        Thread.sleep(2000L)
        selectInTree  'Data Classes'
        click newDataClass
        Thread.sleep(2000l)
        click importedDataElementFromTreeView

        then:
        check rightSideTitle contains 'MET-523.M1.DE1'

    }

    def"delete the created data model"(){

        when:

        selectInTree  'Data Element From'

        then:
        check rightSideTitle contains 'Data Element From MET-2299@0.0.1'

        when:
        click dataMenuBarButton
        click deleteButton
        Thread.sleep(2000l)


        then:
        check modalHeader contains 'Do you really want to delete Data'
        and:
        click modalPrimaryButton

    }
}
