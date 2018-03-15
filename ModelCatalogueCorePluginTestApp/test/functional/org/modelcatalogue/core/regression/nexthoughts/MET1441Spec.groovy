package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class MET1441Spec extends AbstractModelCatalogueGebSpec {
    private static final String myModel = "#my-models"
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String policies = "input#dataModelPolicy"
    private static final String finishButton = "button#step-finish"
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 5000L
    private static final String uuid = UUID.randomUUID().toString()
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String dataModelMenuButton = 'a#role_item_catalogue-element-menu-item-link'
    private static final String finalize = 'a#finalize-menu-item-link'
    private static final String deleteMenu = 'a#delete-menu-item-link'
    private static final String versionNote = 'textarea#revisionNotes'
    private static final String finalizeButton = 'a#role_modal_modal-finalize-data-modalBtn'
    private static final String table = 'tbody.ng-scope>tr:nth-child(1)>td:nth-child(4)'
    private static final String metadataStep = "button#step-metadata"
    private static final String wizardSummary = 'td.col-md-4'
    private static final String exitButton = 'button#exit-wizard'
    private static final String modelInTree = 'ul.catalogue-element-treeview-list-root>li>div>span>span'

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

    def "Click on the green button to complete the Data Model Creation"() {
        when: "Clicked on Green Button"
        fill name with uuid
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check finishButton displayed
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then: "Summary Should display New Data Model Creation Message"
        check '#summary' displayed
        println $("#summary").text()
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

    def "Add Data Classes "() {
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
        fill Common.nameLabel with "NEW_TESTING_MODEL "
        fill Common.modelCatalogueId with "${UUID.randomUUID()}"
        fill Common.description with 'THIS IS MY DATA CLASS'

        then:
        $(metadataStep).isDisplayed()

        when: 'click green button'
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click exitButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check wizardSummary contains "NEW_TESTING_MODEL"
    }

    def "Finalized the Data Model"() {
        when: "Click on the Main Menu and finalized data model"
        click modelInTree
        click dataModelMenuButton
        click finalize

        and:
        fill versionNote with 'THIS IS THE VERSION NOTE'
        click finalizeButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click Common.modalPrimaryButton

        then:
        check table contains "$uuid (0.0.1) finalized"
    }

    def "Check Delete Button must be disabled"() {
        when: "Click on the Main Menu"
//        search uuid
        click modelInTree
        click dataModelMenuButton

        then: "No Option for the delete"
        $(deleteMenu).displayed == false
    }
}