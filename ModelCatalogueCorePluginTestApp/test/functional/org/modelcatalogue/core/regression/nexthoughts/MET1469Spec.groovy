package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class MET1469Spec extends AbstractModelCatalogueGebSpec {
    private static final String myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String metadataStep = "button#step-metadata"
    private static final String label = "textarea#section-label"
    private static final String section_title = "textarea#section-title"
    private static final String instruction = "textarea#section-instructions"
    private static final String page_number = 'input#form-page-number'
    private static final String occurrence = 'ul.nav-pills>li:nth-child(3)>a'
    private static final String finishButton = "button#step-finish"
    private static final String appearance = 'ul.nav-pills>li:nth-child(4)>a'
    private static final String name = "input#local-name"
    private static final String elementStep = "button#step-elements"
    private static final String dataElement = "input#data-element"
    private static final String plusButton = "span.input-group-btn>button"
    private static final String raw = "ul.nav-pills>li:nth-child(4)>a"
    private static final String wizardSummary = 'td.col-md-4'
    private static final String parentStep = "button#step-parents"
    private static final String formSection = 'ul.nav-pills>li:nth-child(1)>a'
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 4000L
    private static final String exitButton = 'button#exit-wizard'
    private static final String nameLabel = "NEW_TESTING_MODEL"
    private static final String saveElement = "a#role_modal_modal-save-elementBtn"
    private static final String tagElement = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(2)"
    private static final String search = "input#dataType"

    static String myName = " testing data element "
    static String myCatalogue = UUID.randomUUID().toString()
    static String myDescription = "This a test element"
    static String tagName = "myTag"
    private final static String dataModel = "Test 1"
    private static
    final String table = "#activity-changes>div.inf-table-body>table>tbody>tr:nth-child(1)>td.inf-table-item-cell.ng-scope.col-md-7"

    def "Login to Model Catalouge"() {

        when: "Login using Curator Account"
        login curator

        then: "My Modal Should be displayed"
        check myModel displayed
    }

    def "Select a finalized Data Model"() {
        when: "Selected an Finalized Data Model"
        select dataModel

        then: "Data Model Page Should Open"
        check modelHeaderName displayed
        check modelHeaderName contains dataModel
    }

    def "login and navigate to the model "() {
        when:
        selectInTree 'Data Classes'

        then:
        check Common.rightSideTitle contains 'Active Data Classes'
    }

    def "Navigate to Create data classes page"() {
        when:
        click Common.create

        then:
        check Common.modalHeader contains "Data Class Wizard"
    }

    def "Create a Data Class and select the created data class"() {
        when: ' fill data class step'
        fill Common.nameLabel with nameLabel
        fill Common.modelCatalogueId with "${UUID.randomUUID()}"
        fill Common.description with 'THIS IS MY DATA CLASS'

        then:
        $(metadataStep).isDisplayed()

        when: 'fill metadata step'
        click metadataStep
        fillMetadata foo: 'one', bar: 'two', baz: 'three', fubor: 'four'

        and: 'click on parent button'
        click parentStep

        then:
        check finishButton displayed

        when: 'fill parent step'
        click formSection
        fill label with 'TEST_LABEL'
        fill section_title with 'MY_TITLE'
        fill instruction with 'this is my instruction'
        fill page_number with '1'

        and: 'click on occurrence '
        click occurrence

        then:
        check finishButton displayed

        when:
        fillMetadata 'Min Occurs': '1', 'Max Occurs': '10'
        // click on appearance
        click appearance
        fill name with ' this is my name'

        then:
        check elementStep displayed

        when: 'fill Element'
        click elementStep
        fill dataElement with 'TEST_ELEMENT'
        click plusButton
        click raw

        and:
        click Common.modalSuccessButton
        fillMetadata foo: 'five'

        and: 'click green button'
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click exitButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check wizardSummary contains "NEW_TESTING_MODEL"
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

        fill search with 'boolean'
        Thread.sleep(2000l)

        and: 'click on the save button'
        click saveElement

        then: 'verify that data is created'
        check wizardSummary contains 'testing data element'
    }

    def "create tag"() {
        when:
        selectInTree 'Tags'

        then:
        check Common.rightSideTitle is 'Active Tags'
    }


    def "navigate to tag creation page"() {
        when:
        click Common.create
        then:
        check Common.modalHeader contains 'Create Tag'
    }

    def "fill tag form"() {
        when:
        fill Common.nameLabel with tagName
        fill Common.description with myDescription
        click saveElement
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        selectInTree 'Tags'

        then:
        check Common.rightSideTitle is 'Active Tags'
        check tagElement is tagName
    }

    def "check history"() {
        when:
        select dataModel
        then:
        check table contains tagName
    }
}