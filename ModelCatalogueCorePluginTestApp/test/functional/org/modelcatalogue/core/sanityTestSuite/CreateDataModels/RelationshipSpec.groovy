package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.module.Select
import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec

import spock.lang.Stepwise

import static org.modelcatalogue.core.gebUtils.Common.description
import static org.modelcatalogue.core.gebUtils.Common.item
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.modelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.nameLabel
import static org.modelcatalogue.core.gebUtils.Common.pick
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save


@Stepwise
class RelationshipSpec  extends AbstractModelCatalogueGebSpec {
    static final String createButton = 'a#role_data-models_create-data-modelBtn'
    static final String summaryWizard = 'div#summary>h4'
    static final String closeButton = 'div.modal-footer>button:nth-child(2)'
    static final String finishStep = 'button#step-finish'
    static final String createAnotherButton = 'div.modal-footer>button:nth-child(1)'
    static final String dataMenuBar = 'a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    static final String newDataClass = 'a#catalogue-element-create-dataClass-menu-item-link>span:nth-child(3)'
    static final String exitButton = 'button#exit-wizard'
    static final String type = 'select#type'
    static final String  childOf = 'ul.nav-tabs>li:nth-child(5)>a>span:nth-child(1)'
    static final String destination = 'input#element'
    static final String createRelationship = 'a#create-new-relationship-menu-item-link>span:nth-child(3)'
    static final String parentOf = 'ul.nav-tabs>li:nth-child(4)>a>span:nth-child(1)'
    static final String destinationRow = '#parentOf-changes > div.inf-table-body > table > tbody > tr > td:nth-child(2) > a'
    static  final String firstRow = '#data-elements-changes > div.inf-table-body > table > tbody > tr > td.inf-table-item-cell.col-md-5 > span:nth-child(3) > span > a'
    static  final String childRow = '#childOf-changes > div.inf-table-body > table > tbody > tr > td > a.preserve-new-lines.ng-binding.ng-scope'
    static  final String businessRules = 'a#catalogue-element-create-validationRule-menu-item-link>span:nth-child(3)'
    static  final String Rules = 'ul.nav-tabs>li:nth-child(8)>a>span:nth-child(1)'
    static  final String rulesRow = '#contextFor-changes > div.inf-table-body > table > tbody > tr > td:nth-child(2) > a'
    static  final String dataElements = 'a#catalogue-element-create-dataElement-menu-item-link>span:nth-child(3)'
    static  final String saveAndCreateAnother = 'a#role_modal_modal-save-and-add-anotherBtn'
    static  final String  dataElementRules = 'ul.nav-tabs>li:nth-child(7)>a>span:nth-child(1)'
    static  final String  dataElementRulesRow = '#involvedIn-changes > div.inf-table-body > table > tbody > tr > td:nth-child(2) > a'
    static  final String  newDataElement = 'tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'


    def"login to model catalogue and create data model"(){

        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
        click createButton

        then:
        check 'div.modal-header>h4' is 'Data Model Wizard'


        when:
        fill nameLabel with 'Relationship'
        fill modelCatalogueId with 'MET-112'
        fill description with 'TESTING'
        Thread.sleep(1000l)

        and:
        click finishStep

        then:
        check summaryWizard is "Data Model Relationship created"


        when:
        click closeButton

        then:
        check rightSideTitle  contains 'Relationship'

    }

    def "create data classes"(){

        when:
        click dataMenuBar
        click newDataClass

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'CLASS_1'
        fill modelCatalogueId with 'MET-001'
        fill description with 'TESTING'
        Thread.sleep(2000l)

        and:
        click finishStep

        then:
        check 'div.alert' contains 'Data Class CLASS_1 created'
        and:
        check 'h4.wizard-summary' contains 'Data Class CLASS_1 created'

        when:
        click createAnotherButton


        then:
        check modalHeader is 'Data Class Wizard'


        when:
        fill nameLabel with 'CLASS_2'
        fill modelCatalogueId with 'MET-002'
        fill description with 'TESTING'
        Thread.sleep(2000l)

        and:
        click finishStep

        then:
        check 'div.alert' contains 'Data Class CLASS_2 created'
        and:
        check 'h4.wizard-summary' contains 'Data Class CLASS_2 created'

        when:
        click createAnotherButton

        then:
        check modalHeader is 'Data Class Wizard'


        when:
        fill nameLabel with 'CLASS_3'
        fill modelCatalogueId with 'MET-003'
        fill description with 'TESTING'
        Thread.sleep(2000l)

        and:
        click finishStep

        then:
        check 'div.alert' contains 'Data Class CLASS_3 created'
        and:
        check 'h4.wizard-summary' contains 'Data Class CLASS_3 created'

        when:
        click createAnotherButton

        then:
        check modalHeader is 'Data Class Wizard'

        when:
        fill nameLabel with 'CLASS_4'
        fill modelCatalogueId with 'MET-004'
        fill description with 'TESTING'
        Thread.sleep(2000l)

        and:
        click finishStep

        then:
        check 'div.alert' contains 'Data Class CLASS_4 created'
        and:
        check 'h4.wizard-summary' contains 'Data Class CLASS_4 created'

        when:
        click exitButton

        then:
        check rightSideTitle contains 'Draft Data'

    }
    def"create a relationship parent of"(){

        when:
        click 'span.mc-name'
        select 'Relationship' open'Data Classes' select'CLASS_1'

        then:
        check rightSideTitle contains 'CLASS_1 MET-001@0.0.1'

        when:
        click dataMenuBar
        click createRelationship

        then:
        check 'h4.ng-binding' is'CLASS_1'

        when:
        fill '#type' with 'parent of'
        fill '#element' with 'CLASS_2'and pick first item
        click modalPrimaryButton

        then:
        check firstRow  contains 'CLASS_2'


        when:
        click parentOf
        Thread.sleep(2000l)

        then:
        check destinationRow is 'CLASS_2 (Relationship 0.0.1)'

    }
    def"create relationship child of "(){

        when:
        click 'span.mc-name'
        select 'Relationship' open'Data Classes' select'CLASS_3'

        then:
        check rightSideTitle contains 'CLASS_3 MET-003@0.0.1'

        when:
        click dataMenuBar
        click createRelationship

        then:
        check 'h4.ng-binding' is'CLASS_3'

        when:
        fill type with 'child of'
        fill destination with 'CLASS_1' and pick first item
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'CLASS_3 MET-003@0.0.1'


        when:
        Thread.sleep(2000L)
        click childOf

        then:
        check childRow is 'CLASS_1 MET-001@0.0.1'



    }
    def"create a relationship provides context for"(){

        when:
        click 'span.mc-name'
        select 'Relationship'

        then:
        check rightSideTitle  contains 'Relationship MET-112@0.0.1'

        when:
        Thread.sleep(2000L)
        click dataMenuBar
        click businessRules


        then:
        check modalHeader is'New Validation Rule'

        when:
        fill 'input#name' with 'patientCare'
        click save
        click 'span.mc-name'
        select 'Relationship' open 'Data Classes' select 'CLASS_4'

        then:
        check rightSideTitle contains 'CLASS_4'

        when:
        Thread.sleep(2000L)
        click dataMenuBar
        click createRelationship

        then:
        check 'h4.ng-binding'  is'CLASS_4'

        when:
        def select = $("#type").module(Select)
        select.selected ="provides context for"

        fill destination with 'patientCare' and pick first item
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'CLASS_4 MET-004@0.0.1'

        when:
        Thread.sleep(2000L)
        click Rules

        then:
        check rulesRow is 'patientCare (Relationship 0.0.1)'

    }
    def"create a data element"(){

        when:
        click 'span.mc-name'
        select 'Relationship'

        then:
        check rightSideTitle contains 'Relationship MET-112@0.0.1'

        when:
        click dataMenuBar
        click dataElements

        then:
        check modalHeader is 'Create Data Element'

        when:
        fill nameLabel with 'ELEMENT_1'
        fill modelCatalogueId with 'MET-01'
        fill description with 'TESTING'
        click saveAndCreateAnother

        then:
        check modalHeader is 'Create Data Element'

        when:
        fill nameLabel with 'ELEMENT_2'
        fill modelCatalogueId with 'MET-02'
        fill description with 'TESTING'
        click save

        then:
        check rightSideTitle contains 'Draft Data'

        when:
        click 'span.mc-name'
        Thread.sleep(2000L)
        select 'Relationship'
        selectInTree 'Data Elements'
        click newDataElement

        then:
        check rightSideTitle contains 'ELEMENT_1 MET-01@0.0.1'

        when:
        Thread.sleep(2000L)
        click dataMenuBar
        click createRelationship

        then:
        check 'h4.ng-binding'  is'ELEMENT_1'

        when:
        fill type with 'is involved in'
        fill destination with 'patientCare' and pick first item
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'ELEMENT_1 MET-01@0.0.1'

        when:
        Thread.sleep(2000L)
        click dataElementRules

        then:
        check dataElementRulesRow is 'patientCare (Relationship 0.0.1)'

    }

}
