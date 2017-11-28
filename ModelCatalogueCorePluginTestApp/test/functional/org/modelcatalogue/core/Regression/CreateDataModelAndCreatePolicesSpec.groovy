package org.modelcatalogue.core.Regression

import groovy.transform.NotYetImplemented
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf

import static org.modelcatalogue.core.geb.Common.description

@IgnoreIf({ !System.getProperty('geb.env') })
class CreateDataModelAndCreatePolicesSpec extends AbstractModelCatalogueGebSpec {

    private static final String create = "a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name = "input#name"
    private static final String createPolicy = "a.create-new-cep-item"
    private static final String version = "input#semanticVersion"
    private static final String catalogueId = "input#modelCatalogueId"
    private static final String icon = "span.search-for-more-icon"
    private static final String importedButton = "button#step-imports"
    private static final String search = "input#dataModelPolicy"
    private static final String savePolicy = "a#role_modal_modal-save-elementBtn"
    private static
    final String allDataElement = "div.basic-edit-modal-prompt>div>div>div:nth-child(2)>form>div:nth-child(2)>p>a:nth-child(4)"
    private static
    final String regularExpression = "div.basic-edit-modal-prompt>div>div>div:nth-child(2)>form>div:nth-child(2)>p>a:nth-child(5)"
    final String policyName = "#name"
    private static final String finishButton = "button#step-finish"
    private static String models = "My First Test"
    private static String VERSION = "0.0.1"
    private static String catalogue = "MET-89765}"


    void addPolicies() {

        when: 'login to model catalogue'
        loginAdmin()
        then:
        check create isDisplayed()

        when: 'click on create and fill form'
        click create
        fill name with models
        fill version with VERSION

        and:
        fill catalogueId with catalogue
        fill search with 'tester'
        click createPolicy

        and: 'create data policy'
        Thread.sleep(3000L)
        fill policyName with 'TESTING_POLICY'

        and:
        click allDataElement
        click regularExpression

        and:
        click savePolicy

        and:
        fill description with 'Testing_policy'
        click importedButton

        and:
        click icon
        Thread.sleep(3000L)

        and:
        selectInSearch(2)
        click finishButton

        then:
        noExceptionThrown()


    }
}
