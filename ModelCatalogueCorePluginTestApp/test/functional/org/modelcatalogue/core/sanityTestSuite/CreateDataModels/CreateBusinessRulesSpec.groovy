package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.*

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
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
    private static final String  ICON="div.modal-body>form>div:nth-child(5)>span>span"
    private static final String SEARCH="input#value"
    private static final String modelCatalogue="span.mc-name"
    private static final String table ="td.col-md-4"
    private static final String businessRule ="td.col-md-4>span>span>a"
    private static final String validationRuleButton ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String deleteButton ="a#delete-menu-item-link>span:nth-child(3)"

    def "login to model catalogue and select business rules"(){
        when:
        loginAdmin()
        select'Test 3' select 'Business Rules'
        selectInTree "Business Rules"

        then:
        check rightSideTitle  contains 'Active Validation Rules'
    }

     def "Navigate to business rules page"(){
         when:
         click create
         then:
         check modalHeader is "New Validation Rule"
     }

     def "fill the form and save business rule"() {
        when:
        fill nameLabel with "my validation Rule ${System.currentTimeMillis()}"
        fill component with "component ${System.currentTimeMillis()}"
        fill focus with "make life better${System.currentTimeMillis()}"
        click ICON
        fill SEARCH with "data" and pick first item
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

    def "delete the validation rules from the data model"(){
        when:
        click modelCatalogue

        and:
        select'Test 3' select 'Business Rules'
        selectInTree "Business Rules"

        then:
        check rightSideTitle  contains 'Active Validation Rules'

        when:
        click businessRule

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
