package org.modelcatalogue.core.Regression

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class CreateDataModelAndCreatePolicesSpec extends AbstractModelCatalogueGebSpec {
    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String version="input#semanticVersion"
    private static final String catalogueId="input#modelCatalogueId"
    private static final String policies="input#dataModelPolicy"
    private static final String moreIcon="span.search-for-more-icon"
    private static final String uniqueOfKind ="div.list-group>a:nth-child(2)"
    private static final String createNew="a.create-new-cep-item"
    private static final String nameP="div.basic-edit-modal-prompt>div>div>div:nth-child(2)>form>div:nth-child(1)>input"
    private static final String policyText="textarea#policyText"
    private static final String save ="a#role_modal_modal-save-elementBtn"
    private static String models  ="My First Test ${System.currentTimeMillis()}"
    private static String VERSION="2.1.0"
    private static String  catalogue ="333 ${System.currentTimeMillis()}"
    private static String mypolicies=" my rule"
    private static String nameP2   = "My Test ${System.currentTimeMillis()}"
    private static String POLICYTEXT="SOME POLICY TEXT"


    void addPolicies(){
        when:
             // login to model catalogue
             loginCurator()
        then:
             noExceptionThrown()

        when:
              // click on create
               click create
               // type a name . please change value
               fill(name)with(models)
               // type a version
               fill(version)with(VERSION)
               // type a id
               fill(catalogueId)with(catalogue)
               //click om more icon
                click moreIcon
                // selectRelation unique of kind
               click uniqueOfKind
               // enter policies
               fill(policies)with(mypolicies)
               // Select create new
              click createNew
              // type policy name
              fill(nameP)with(nameP2)
             // type policy
              fill(policyText)with(POLICYTEXT)
               // CLICK ON SAVE
                click save


        then:
             noExceptionThrown()


    }
}
