package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Ignore
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.*


@Stepwise
class CreateBusinessRulesSpec extends AbstractModelCatalogueGebSpec {

    private static final String  component="input#component"
    private static final String  focus="input#ruleFocus"
    private static final String  trigger="input#trigger"
    private static final String  rule="textarea#rule"
    private static final String  errorCondition="input#errorCondition"
    private static final String  issueRecord="input#issueRecord"
    private static final String  notification="input#notification"
    private static final String  target="input#notificationTarget"
    private static final String modelCatalogue="span.mc-name"
    private static final String table ="td.col-md-4"
    private static final String businessRule ="td.col-md-4>span>span>a"
    private static final String validationRuleButton ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String deleteButton ="a#delete-menu-item-link>span:nth-child(3)"
    private static final String stepMetadata = "button#step-metadata"
    private static final String stepChildren = "button#step-children"
    private static final String dataElements = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(2)>div>span>span"
    private static final String stepFinish = "#step-finish"
    private static final String exitButton = "#exit-wizard"
    private static final String wizardSummary = '.wizard-summary'
    private static final String NEW_DATA_CLASS_NAME = "New Data Class"
    private static final String  saveElement = "a#role_modal_modal-save-elementBtn"
    private static final String  businessRules = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(5)>div>span>span"



    def"login to model catalogue and select business rules"(){

        when:
        loginAdmin()
        select'Test 3' select 'Data Classes'

        then:
        check rightSideTitle  contains 'Active Data Classes'
    }

    def"create a data Class"(){
        click create
        expect: 'the model dialog opens'
        check modalDialog displayed

        when: 'the model details are filled in'
        fill nameLabel with NEW_DATA_CLASS_NAME
        fill modelCatalogueId with "34"
        fill description with "Description"

        then: 'metadataStep step is not disabled'
        check stepMetadata enabled

        when: 'metadataStep step is clicked'
        click stepMetadata

        then:
        check stepMetadata has 'btn-primary'

        when: 'metadataStep are filled in'
        fillMetadata foo: 'bar', one: 'two'

        and: 'children step is clicked'
        click stepChildren

        then:
        check stepChildren has 'btn-primary'

        when: 'finish is clicked'
        click stepFinish

        then: 'the data class is saved'
        check wizardSummary is "Data Class ${NEW_DATA_CLASS_NAME} created"

        when:
        click exitButton

        and:
        selectInTree 'Data Classes', true

        then:
        check CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith(NEW_DATA_CLASS_NAME)) displayed

        check modalDialog gone

    }

    def"create a data element "(){

        when:
        selectInTree 'Data Elements'
        click create

        and:
        fill nameLabel with 'New Data Element'

        fill modelCatalogueId with 'MET-56'

        fill description with 'TESTING'

        and: 'select a data type'

        fill 'input#dataType' with ' boolean'
        Thread.sleep(2000l)
        remove messages

        and: 'click on the save button'
        click saveElement

        then: 'verify that data is created'
        check 'td.col-md-4' contains 'New Data Element'

    }
     def" Navigate to business rules page"(){

         when:
         selectInTree 'Business Rules'
         click create
         then:
         check modalHeader is "New Validation Rule"
     }
    def" fill the form and save business rule"() {
        when:
        fill nameLabel with "my validation Rule ${System.currentTimeMillis()}"
        fill component with "component ${System.currentTimeMillis()}"
        fill focus with "make life better${System.currentTimeMillis()}"
        fill 'input#dataClasses' with'New Data Class' and pick first item
        Thread.sleep(2000l)
        fill 'input#dataElements' with'New Data Element' and pick first item
        Thread.sleep(2000l)
        fill trigger with "trigger"
        fill rule with "my first rule"
        fill errorCondition with "this error 12 ${System.currentTimeSeconds()}"
        fill issueRecord with "no issue"
        fill notification with "2 week"
        fill target with "1 week"
        fill description with "rare disease"
        fill modelCatalogueId with "MET -${System.currentTimeSeconds()}"
        click save

        then:
        check table contains 'my validation Rule'




    }

    def"delete the validation rules from the data model "(){

        when:
        click modelCatalogue
        and:
        select'Test 3' select 'Business Rules'
        selectInTree "Business Rules"

        then:
        check rightSideTitle  contains 'Active Validation Rules'

        when:
        click businessRule

        then:
        check { infTableCell(1, 1) } contains "New Data Class"


        and:'navigate to the top menu and click on the validation button'
        click validationRuleButton

        and:
        click deleteButton

        and:'confirm the deletion'
        click modalPrimaryButton

        then:
        Thread.sleep(2000L)
        check table gone

    }


}
