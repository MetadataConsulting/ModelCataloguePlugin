package org.modelcatalogue.core.regression.nexthoughts

import spock.lang.Stepwise
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.Common
import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.modalSuccessButton
import spock.lang.Ignore

@Stepwise
@Ignore
class ReadOnlyDataClassSpec extends AbstractModelCatalogueGebSpec {
    private static final String metadataStep = "button#step-metadata"
    private static final String finishButton = "button#step-finish"
    private static final String parentStep = "button#step-parents"
    private static final String formSection = 'ul.nav-pills>li:nth-child(1)>a'
    private static final String section_title = "textarea#section-title"
    private static final String label = "textarea#section-label"
    private static final String instruction = "textarea#section-instructions"
    private static final String page_number = 'input#form-page-number'
    private static final String occurrence = 'ul.nav-pills>li:nth-child(3)>a'
    private static final String appearance = 'ul.nav-pills>li:nth-child(4)>a'
    private static final String elementStep = "button#step-elements"
    private static final String dataElement = "input#data-element"
    private static final String plusButton = "span.input-group-btn>button"
    private static final String raw = "ul.nav-pills>li:nth-child(4)>a"
    private static final String name = "input#local-name"
    private static final String wizardSummary = 'td.col-md-4'
    private static final String exitButton = 'button#exit-wizard'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000L
    private static final String dataModel = "a#role_item_catalogue-element-menu-item-link"
    private static final String cloneCurrentButton = "a#clone-menu-item-link>span:nth-child(3)"

    def "login and navigate to the model "() {
        when:
        loginCurator()
        select 'Test 2'
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
        fill Common.nameLabel with "NEW_TESTING_MODEL "
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

    def "On the top menu bar, Click on the Data Model link"() {

        when: "Search for the New Test"
        $("a.mc-name-parent").click()
        select 'Test 1'
        println "model has been selected"
        def menu = $(dataModel)
        menu.click()
        println("Model menu has been clicked")
        println cloneCurrentButton
        click cloneCurrentButton


        then:
        check Common.modalDialog displayed
        check Common.modalHeader contains "Clone"

        when: "Search for the other Data Model"
        searchDataModel 'Test 1'
        $("div.modal-footer").children("button.btn-primary").click()

        then:
        check Common.modalDialog displayed
        check Common.modalHeader contains "Login"
    }
}