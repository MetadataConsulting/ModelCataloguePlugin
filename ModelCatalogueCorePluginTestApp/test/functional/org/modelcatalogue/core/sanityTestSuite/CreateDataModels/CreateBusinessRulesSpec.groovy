package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import org.openqa.selenium.Keys
import static org.modelcatalogue.core.geb.Common.*


@Stepwise
class CreateBusinessRulesSpec extends AbstractModelCatalogueGebSpec {
    private static final String dataModel="a#role_item_catalogue-element-menu-item-link>span:nth-child(3"
    private static final String businessRule="a#catalogue-element-create-validationRule-menu-item-link>span:nth-child(3)"
    private static final String  name="input#name"
    private static final String  title="div.modal-header>h4"
    private static final String  component="input#component"
    private static final String  focus="input#ruleFocus"
    private static final String  trigger="input#trigger"
    private static final String  rule="textarea#rule"
    private static final String  errorCondition="input#errorCondition"
    private static final String  issueRecord="input#issueRecord"
    private static final String  notification="input#notification"
    private static final String  target="input#notificationTarget"
    private static final String  catologueId="input#modelCatalogueId"
    private static final String  ICON="div.modal-body>form>div:nth-child(5)>span>span"
    private static final String SEARCH="input#value"




    def"login to model catalogue and select business rules"(){

        when:
        loginCurator()
        select'TEST5'
        selectInTree "Business Rules"

        then:
        check rightSideTitle  contains 'Active Validation Rules'
    }
     def" Navigate to business rules page"(){
         when:
              click create
         then:
              check modalHeader is "New Validation Rule"
     }
    def" fill the form and save business rule"() {
        when:
             fill nameLabel with "my validation Rule ${System.currentTimeMillis()}"
             fill component with "component ${System.currentTimeMillis()}"
             fill focus with "make life better${System.currentTimeMillis()}"
             click ICON
             fill SEARCH  with "data" and pick first item
             fill trigger with "trigger"
             fill rule  with "my first rule"
             fill errorCondition  with "this error 12 ${System.currentTimeSeconds()}"
             fill issueRecord  with "no issue"
             fill notification with "2 week"
             fill target with "1 week"
             fill description with "rare disease"
             fill modelCatalogueId with "MET -${System.currentTimeSeconds()}"
             click save

        then:
             noExceptionThrown()



    }


}
