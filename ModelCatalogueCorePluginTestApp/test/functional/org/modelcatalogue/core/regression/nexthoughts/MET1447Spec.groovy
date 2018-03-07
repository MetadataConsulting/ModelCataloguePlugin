package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class MET1447Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String policies = "input#dataModelPolicy"
    private static final String finishButton = "#step-finish"
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 5000L
    private static final String uuid = UUID.randomUUID().toString()
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String first_row = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"
    private static final String dataTypeName = 'TypeMET1447'
    private static final String search = "input#dataType"
    private static final String saveElement = "a#role_modal_modal-save-elementBtn"
    static String myName = " testing data element"
    static String myCatalogue = UUID.randomUUID().toString()
    static String myDescription = "This a test element"
    private static final String dataTypeCreated = 'tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'

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

    def "Create a DataType"() {
        when:
        selectInTree 'Data Types'

        then:
        check Common.rightSideTitle contains 'Active Data Types'
    }

    def "navigate to data type creation page "() {
        when:
        click Common.create

        then:
        check Common.modalHeader contains 'Create Data Type'
    }

    def "fill the create data type form"() {
        when:
        fill Common.nameLabel with dataTypeName
        fill Common.description with "my description of data type"
        click Common.save

        then:
        check first_row contains dataTypeName
    }

    def "Create Data Element"() {
        when:
        selectInTree 'Data Elements'

        then:
        check Common.rightSideTitle is 'Active Data Elements'
    }

    def "navigate to data element creation page"() {
        when:
        click Common.create
        then:
        check Common.modalHeader contains 'Create Data Element'
    }

    def "fill the create data element form"() {
        when:
        fill Common.nameLabel with myName
        fill Common.modelCatalogueId with myCatalogue
        fill Common.description with myDescription

        and: 'select a data type'
        fill search with dataTypeName and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check saveElement displayed

        and: 'click on the save button'
        click saveElement
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        selectInTree 'Data Elements'

        then: 'verify that data is created'
        $(dataTypeCreated).text()?.trim() == myName.trim()
    }
}