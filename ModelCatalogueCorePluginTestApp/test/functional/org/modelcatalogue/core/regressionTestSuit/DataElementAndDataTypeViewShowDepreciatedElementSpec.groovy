package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.gebUtils.Common.getDescription
import static org.modelcatalogue.core.gebUtils.Common.getModelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.getNameLabel
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save


@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1270")
class DataElementAndDataTypeViewShowDepreciatedElementSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String  saveAndCreateAnother = "a#role_modal_modal-save-and-add-anotherBtn"
    private static final String header = "h4.ng-binding"
    private static final String createNewDataElement = "a#catalogue-element-create-dataElement-menu-item-link>span:nth-child(3)"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String dataMenuBarButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String createNewDataType = "a#catalogue-element-create-dataType-menu-item-link>span:nth-child(3)"
    private static final String modelCatalogueButton = "span.mc-name"
    private static final String simpleType1= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String deprecated= "a#archive-menu-item-link>span:nth-child(3)"
    private static final String element1= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String table1= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(2)>a"
    private static final String table2= "tbody.ng-scope>tr:nth-child(2)>td:nth-child(2)>a"
    private static final String dataTypes= "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>div>span>span"
    private static final String dataElements= "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(2)>div>span>span"
    private static final String dataModel= "ul.catalogue-element-treeview-list-root>li>div>span>span"
    private static final String deleteButton= "a#delete-menu-item-link>span:nth-child(3)"
    private static final String deprecatedButton= "button.btn-primary"

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
        fill nameLabel with 'ElementAndType Model'
        fill modelCatalogueId with 'MEM-002'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model ElementAndType Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'ElementAndType Model'


    }

    def"create data types"(){

        when:
        click dataMenuBarButton
        click createNewDataType

        then:
        check header is 'Create Data Type'

        when:
        fill nameLabel with 'SIMPLE TYPE1'
        fill modelCatalogueId with 'MD-0054'
        fill description with 'TESTING DATA TYPE'
        Thread.sleep(2000L)
        click saveAndCreateAnother

        then:
        check header is 'Create Data Type'

        when:
        fill nameLabel with 'SIMPLE TYPE2'
        fill modelCatalogueId with 'MD-0055'
        fill description with 'TESTING DATA TYPE'
        Thread.sleep(2000L)
        click saveAndCreateAnother

        then:
        check header is 'Create Data Type'

        when:
        fill nameLabel with 'SIMPLE TYPE3'
        fill modelCatalogueId with 'MD-0056'
        fill description with 'TESTING DATA TYPE'
        Thread.sleep(2000L)
        click save

        then:
        check rightSideTitle contains 'Draft Data'

    }

 def"created data elements"(){

     when:
     click modelCatalogueButton
     select 'ElementAndType Model'

     then:
     check rightSideTitle contains 'ElementAndType Model MEM-002@0.0.1'

     when:
     click dataMenuBarButton
     click createNewDataElement

     then:
     check modalHeader is 'Create Data Element'

     when:
     fill nameLabel with 'ELEMENT1'
     fill modelCatalogueId with 'ME-005'
     fill description with 'TESTING DATA TYPE'
     Thread.sleep(2000L)
     click saveAndCreateAnother

     then:
     check modalHeader is 'Create Data Element'


     when:
     fill nameLabel with 'ELEMENT2'
     fill modelCatalogueId with 'ME-006'
     fill description with 'TESTING DATA TYPE'
     Thread.sleep(2000L)
     click saveAndCreateAnother

     then:
     check modalHeader is 'Create Data Element'


     when:
     fill nameLabel with 'ELEMENT3'
     fill modelCatalogueId with 'ME-007'
     fill description with 'TESTING DATA TYPE'
     Thread.sleep(2000L)
     click save

     then:
     check rightSideTitle contains 'Draft Data'

 }
  def"deprecate a data type"(){

      when:
      click modelCatalogueButton
      select 'ElementAndType Model'
      selectInTree 'Data Types'

      then:
      check rightSideTitle is 'Active Data Types'


      when:
      click simpleType1

      then:
      check rightSideTitle contains 'SIMPLE TYPE1 MD-0054@0.0.1'

      when:
      click dataMenuBarButton
      click deprecated
      click deprecatedButton

      then:
      check rightSideTitle contains 'SIMPLE TYPE1 MD-0054@0.0.1'

  }

    def "deprecate a data element"(){
        when:
        click modelCatalogueButton
        select 'ElementAndType Model'
        selectInTree 'Data Elements'

        then:
        check rightSideTitle is 'Active Data Elements'


        when:
        click element1

        then:
        check rightSideTitle contains 'ELEMENT1 ME-005@0.0.1'

        when:
        click dataMenuBarButton
        click deprecated
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'ELEMENT1 ME-005@0.0.1'
    }

    def"check that data type and element are deprecated"(){

        when:
        selectInTree 'Deprecated Items'

        then:
        check rightSideTitle  contains 'Deprecated Catalogue Elements'


        expect:
        Thread.sleep(2000L)
        check table1 is 'ELEMENT1'
        check table2  is 'SIMPLE TYPE1'

    }

    def"check that deprecated data type does not figure out on the list of data types"(){

        when:
        Thread.sleep(2000l)
        click dataTypes

        then:
        check rightSideTitle is 'Active Data Types'


        expect:
        check { infTableCell(1, 1) } is 'SIMPLE TYPE2 MD-0055'
        check { infTableCell(2, 1) } is 'SIMPLE TYPE3 MD-0056'


    }

    def"check that the deprecated data element does not appear on the list of data elements"(){

        when:
        click dataElements

        then:
        check rightSideTitle is 'Active Data Elements'

        expect:
        Thread.sleep(2000L)
        check { infTableCell(1, 1) } is 'ELEMENT2 ME-006'
        check { infTableCell(2, 1) } is 'ELEMENT3 ME-007'

    }
    def "delete the created data model"(){

        when:
        click dataModel

        then:
        check rightSideTitle contains 'ElementAndType Model MEM-002@0.0.1'

        when:
        click dataMenuBarButton
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Data Model ElementAndType Model?'

        and:
        click modalPrimaryButton
    }

}
