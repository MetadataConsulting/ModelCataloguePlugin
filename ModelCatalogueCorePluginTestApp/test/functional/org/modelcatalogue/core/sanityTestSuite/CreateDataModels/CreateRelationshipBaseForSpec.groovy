package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@Stepwise
class CreateRelationshipBaseForSpec extends  AbstractModelCatalogueGebSpec {

    static final String createButton = 'a#role_data-models_create-data-modelBtn'
    static final String summaryWizard = 'div#summary>h4'
    static final String  closeButton = 'div.modal-footer>button:nth-child(2)'
    static final String    dataClass = 'tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'
    static final String   finishStep = 'button#step-finish'
    static final String    relationshipButton = 'a#create-new-relationship-menu-item-link>span:nth-child(3)'
    static final String   createAnotherButton = 'div.modal-footer>button:nth-child(1)'
    static final String    exitButton = 'button#exit-wizard'
    static final String   dataMenuBar = 'a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    static final String    isBaseFor = 'ul.nav-tabs>li:nth-child(2)>a>span:nth-child(1)'
    static final String    newDataClass = 'a#catalogue-element-create-dataClass-menu-item-link>span:nth-child(3)'
    static final String dataModel = 'DATA_MODEL_1'


    def" login to model catalogue"(){

        when:
        loginAdmin()

        then:
        check createButton isDisplayed()
    }
    def"create a data model "(){

        when:
        click createButton

        then:
        check 'div.modal-header>h4' is 'Data Model Wizard'


        when:
        fill nameLabel with dataModel
        fill modelCatalogueId with 'MET-112'
        fill description with 'TESTING'
        Thread.sleep(3000l)

        and:
        click finishStep

        then:
        check summaryWizard is "Data Model ${dataModel} created"


        when:
        click closeButton

        then:
        check rightSideTitle  contains  dataModel
    }

    def " create data classes"(){

        when:
        click dataMenuBar
        click newDataClass

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'DATACLASS_1'
        fill modelCatalogueId with 'MET-003'
        fill description with 'TESTING'
        Thread.sleep(3000l)

        and:
        click finishStep

        then:
        check 'div.alert' contains 'Data Class DATACLASS_1 created'
        and:
        check 'h4.wizard-summary' contains 'Data Class DATACLASS_1 created'

        when:
        click createAnotherButton

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'DATACLASS_2'
        fill modelCatalogueId with 'MET-004'
        fill description with 'TESTING'
        Thread.sleep(3000l)

        and:
        click finishStep

        then:
        check 'div.alert' contains 'Data Class DATACLASS_2 created'
        and:
        check 'h4.wizard-summary' contains 'Data Class DATACLASS_2 created'

        when:
        click exitButton
        Thread.sleep(2000L)
        click 'span.mc-name'
        Thread.sleep(2000l)
        select 'DATA_MODEL_1' open 'Data Classes'

        then:
        check rightSideTitle is 'Active Data Classes'

        and:
        check dataClass isDisplayed()

    }
    def"create relationship based for"(){

        when:
        click dataClass

        then:
        check rightSideTitle contains 'DATACLASS_1'


        when:
        click dataMenuBar
        click relationshipButton

        then:
        check 'h4.ng-binding' is 'DATACLASS_1'

        when:
        fill'#type' with 'is base for'
        fill 'input#element' with 'DATACLASS_2' and pick first item

        click modalPrimaryButton

        then:
        check rightSideTitle contains 'DATACLASS_1'

        when:
        click isBaseFor
        Thread.sleep(2000l)

        then:
        check { infTableCell(1, 2) } is 'DATACLASS_2 (DATA_MODEL_1 0.0.1)'

    }
}
