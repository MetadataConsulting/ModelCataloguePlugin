package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
//@Ignore
class MET1478Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String policies = "input#dataModelPolicy"
    private static final String removeTag = "a.remove-tag"
    private static final String policyTag = "span.with-pointer.ng-binding"

    def "Login to Model Catalouge"() {

        when: "Login using Curator Account"
        login curator

        then: "My Modal Should be displayed"
        check myModel displayed
        check create displayed
    }

    def "Create a Data model using Create button"() {
        when: "Click on the create button"
        click create

        then: "Data model popup should open"
        check modalHeader contains "Data Model Wizard"
    }

    def "Fill the form to Create Data Model"() {
        when: "Fill the form "
        click removeTag
        fill policies with "c" and Common.pick first Common.item
        String firstPolicy = $(policyTag).text()
        println("??????????????????$firstPolicy????????????????")

        then: "Check the policies are displayed"
        check policies displayed

        when: "Fill the form "
        click removeTag
        fill policies with "C" and Common.pick first Common.item
        String secondPolicy = $(policyTag).text()
        println("??????????????????$secondPolicy????????????????")

        then: "Check the policies are displayed"
        check policies displayed
        assert firstPolicy == secondPolicy
    }
}