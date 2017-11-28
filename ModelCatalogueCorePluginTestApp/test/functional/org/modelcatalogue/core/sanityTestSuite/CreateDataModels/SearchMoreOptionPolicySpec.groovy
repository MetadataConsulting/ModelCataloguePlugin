package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import static org.modelcatalogue.core.geb.Common.getModalCloseButton
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.pick

@IgnoreIf({ !System.getProperty('geb.env') })
class SearchMoreOptionPolicySpec extends AbstractModelCatalogueGebSpec{

    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String policies="input#dataModelPolicy"
    private static final String  searchForData="input#value"
    private static final String button_x="div.input-group-addon"
    private static final String exit="button#exit-wizard>span:nth-child(1)"
    private static final String modelCatalogue="span.mc-name"
    private static String my_name ="first test"
    private static String policyText="tester"
    private static String  searchData="TESTING_POLICY"

    @Ignore
    def "search option"() {

        when:
         loginAdmin()
        then:
        check modelCatalogue displayed

        when:'click on create and fill the form'
        click create
        fill name with my_name
        fill policies  with policyText and pick first item

        and:'search for a policy'
        fill searchForData  with searchData and pick first item

        and:''
        click button_x
        click exit
        click modalCloseButton

        then:
        check modelCatalogue displayed


    }

}
