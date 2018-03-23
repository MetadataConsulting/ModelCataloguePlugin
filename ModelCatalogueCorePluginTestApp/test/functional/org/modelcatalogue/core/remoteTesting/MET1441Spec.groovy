package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataClassesPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction

@Stepwise
class MET1441Spec extends AbstractModelCatalogueGebSpec {
    @Shared
    String uuid = UUID.randomUUID().toString()


    private static final myModel = "#my-models"
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String name = "input#name"
    private static final String policies = "input#dataModelPolicy"
    private static final String finishButton = "button#step-finish"
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 5000L
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

    @Issue('https://metadata.atlassian.net/browse/MET-1441')
    def "Login to Model Catalouge"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.submit()

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.titleContains uuid

        when:
        dataModelPage.treeView.select('Data Classes')

        then:
        at DataClassesPage

        when:
        click Common.create
        true

        then:
        check Common.modalHeader contains "Data Class Wizard"

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