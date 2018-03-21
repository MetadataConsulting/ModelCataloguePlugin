package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise

@Stepwise
class MET1604Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String policies = "input#dataModelPolicy"
    private static final String finishButton = "#step-finish"
    private static final String exitButton = "#exit-wizard"
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 5000L
    private static final String uuid = UUID.randomUUID().toString()
    private static final String modelHeaderName = 'h3.ce-name'

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
        fill name with uuid
        fill policies with "c" and pick first item

        then: "Check the policies are displayed"
        check policies displayed
    }

    def "Click on the green button to complete the Data Model Creation"() {
        when: "Clicked on Green Button"
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then: "Summary Should display New Data Model Creation Message"
        check '#summary' displayed
        $("#summary").text()
        check '#summary' contains uuid
    }

    def "When close the data model then new created model should open"() {
        when: "Click on the close button of model"

        click Common.modalCloseButton

        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then: "New Created Data Model Page should open"
        check modelHeaderName displayed
        check modelHeaderName contains uuid
    }
}