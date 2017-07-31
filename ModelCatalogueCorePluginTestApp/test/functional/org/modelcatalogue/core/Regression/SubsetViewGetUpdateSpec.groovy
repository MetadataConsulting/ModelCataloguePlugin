package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.getBackdrop
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1054?filter=11400")
class SubsetViewGetUpdateSpec extends AbstractModelCatalogueGebSpec{

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String dataMenuBarButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  createDataType = "a#catalogue-element-create-dataType-menu-item-link>span:nth-child(3)"
    private static final String  header = "h4.ng-binding"
    private static final String pickEnumeratedType = '#pickEnumeratedType'
    private static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    private static final String modelCatalogueButton = "span.mc-name"
    private static final String newDataType = "a#role_list_create-catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String baseEnumeration = '#baseEnumeration'
    private static final String pickSubset = '#pickSubsetType'
    private static final String deleteButton = 'a#delete-menu-item-link>span:nth-child(3)'

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
        fill nameLabel with 'Subset Model'
        fill modelCatalogueId with 'MEM-002'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Subset Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Subset Model'
    }

    def"create an enumerated data type"(){

        when:
        click dataMenuBarButton
        click createDataType

        then:
        check header is 'Create Data Type'

        fill nameLabel with 'ENUMERATE TYPE1'
        fill modelCatalogueId with 'MD-002'
        fill description with 'TESTING DATA TYPE'

        click pickEnumeratedType
        Thread.sleep(2000L)

        then:
        check enumerationsTableEditor displayed

        when:
        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five','06':'six','07':'seven'

        click save

        then:
        check modalDialog gone
        check rightSideTitle contains 'Draft Data'


        when:
        click modelCatalogueButton
        select 'Subset Model'open 'Data Types'

        then:
        check { infTableCell(1, 1) } contains "ENUMERATE TYPE1"
        check { infTableCell(1, 2) } contains "01: one\n" + "02: two\n" + "03: three\n" + "04: four\n" + "05: five\n" + "06: six\n" + "07: seven"

    }

    def"create a subset data type and verify that the selected enum are displayed "(){

        when:
        click newDataType

        then:
        check header is 'Create Data Type'

        when:

        fill nameLabel with 'SUBSET TYPE1'
        fill modelCatalogueId with 'MD-003'
        fill description with 'TESTING DATA TYPE'
        click pickSubset


        then:
        check baseEnumeration displayed

        when:
        fill baseEnumeration with 'ENUMERATE TYPE1' and pick first item
        click 'input#subtype-enum-1'
        click 'input#subtype-enum-2'
        click 'input#subtype-enum-5'
        click 'input#subtype-enum-6'
        click save


        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "SUBSET TYPE1"
        check { infTableCell(1, 2) } contains '01: one\n' + '02: two\n' + '05: five\n' + '06: six'

    }

    def"delete the created data model"(){

        when:
        click modelCatalogueButton
        select 'Subset Model'

        then:
        check rightSideTitle contains 'Subset Model MEM-002@0.0.1'

        when:
        click dataMenuBarButton
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Data Model Subset Model?'
        and:
        click modalPrimaryButton
    }


}
