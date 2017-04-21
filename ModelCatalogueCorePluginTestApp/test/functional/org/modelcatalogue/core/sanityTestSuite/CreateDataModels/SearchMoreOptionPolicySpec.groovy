package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.Keys

import static org.modelcatalogue.core.geb.Common.getModalCloseButton

class SearchMoreOptionPolicySpec extends AbstractModelCatalogueGebSpec{
    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String policies="input#dataModelPolicy"
    private static final String searchMore="a.show-more-cep-item"
    private static final String  searchForData="input#value"
    private static final String button_x="div.input-group-addon"
    private static final String exit="button#exit-wizard>span:nth-child(1)"
    private static String my_name ="first test"
    private static String policyText="tester"
    private static String  searchData="My new rule"

    def" search option"(){

        when:
             loginCurator()
        then:
             noExceptionThrown()

        when:
             // click on create
               click create
              // type a name . please change value
               fill(name)with(my_name)
                // TYPE POLICY
              fill(policies)with(policyText)
             // selectRelation search more
               click searchMore
               fill(searchForData)with(searchData)
                // click on X
                click button_x
               // click on exit
                click exit

        then:
             noExceptionThrown()
            click modalCloseButton





    }

}
