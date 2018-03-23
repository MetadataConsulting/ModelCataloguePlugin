package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common
import spock.lang.Issue

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise

import spock.lang.Ignore

@Stepwise
//@Ignore
class MET1650Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String tableImported = "td.col-md-5"
    private static final String removeButton = "a#role_item_remove-relationshipBtn"
    private static final String plusButton = "span.fa-plus-square-o"
    private static final String modelCatalogue = "span.mc-name"
    private static final String  importedDataModel= "td.col-md-5"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

    static String selectModelToEdit = "Test 1"

    @Issue('https://metadata.atlassian.net/browse/MET-1650')
    def "Login to Model Catalouge"() {

        when: "Login using Curator Account"
        login curator

        then: "My Modal Should be displayed"
        check myModel displayed
    }

    def "Select a finalized Data Model"() {
        when: "Selected an Finalized Data Model"
        select selectModelToEdit

        then: "Data Model Page Should Open"
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