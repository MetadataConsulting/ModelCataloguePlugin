package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
@Issue('https://metadata.atlassian.net/browse/MET-1650')
@Title('Remove the imported data model appears in the list of Activity')
@Narrative('''
- Login to model catalogue (gel)
-  Select a draft model
- Navigate to Import By tag
-  Import a data model
- Check that the imported data model appears on the list of activity
- Remove the imported data model
''')
class MET1650Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String tableImported = "td.col-md-5"
    private static final String removeButton = "a#role_item_remove-relationshipBtn"
    private static final String plusButton = "span.fa-plus-square-o"
    private static final String modelCatalogue = "span.mc-name"
    private static final String  importedDataModel= "td.col-md-5"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

    @Shared
    String selectModelToEdit = "Test 1"

//    def "Login to model catalogue (gel)"() {
//
//    }
//
//    def "select a draft model"() {
//
//    }
//    def "Navigate to Import By tag"() {
//
//    }
//    def "Import a data model"() {
//
//    }
//    def "Check that the imported data model appears on the list of activity"() {
//
//    }
//    def "Remove the imported data model"() {
//
//    }
//    def "Check that Remove the imported data model appears on the list of Activity"() {
//
//    }

    def "Login to Model Catalouge"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "Select a finalized Data Model"() {
        when: "Selected an Finalized Data Model"
        DashboardPage dashboardPage = browser.page(DashboardPage)
        dashboardPage.select(selectModelToEdit)

        then:
        at DataModelPage

        and: "Data Model Page Should Open"
        check modelHeaderName displayed
        check modelHeaderName contains selectModelToEdit
    }

    def "Select Imported Data Models"() {
        when:
        selectInTree 'Imported Data Models'

        then:
        check Common.rightSideTitle contains "$selectModelToEdit Imports"
    }


    def "import model "() {
        when:
        addDataModelImport 'Clinical Tags'

        then:
        check importedDataModel contains 'Clinical Tags'
    }

    def "delete the imported data model"() {

        when: 'navigate back to the main page'
        click modelCatalogue
        
        and:
        select selectModelToEdit
        selectInTree 'Imported Data Models'

        then: 'verify the title'
        check Common.rightSideTitle contains "$selectModelToEdit Imports"

        when: 'click on the plus button'
        click plusButton

        and: 'remove the imported data models'
        click removeButton

        and: 'click on the ok button'
        click Common.modalPrimaryButton

        then: 'verify that imported is removed'
        check tableImported gone
    }
}