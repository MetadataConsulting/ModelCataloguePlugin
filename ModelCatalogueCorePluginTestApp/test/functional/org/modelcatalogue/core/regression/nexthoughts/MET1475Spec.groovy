package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class MET1475Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    static String selectModelToEdit = "Test 1"
    private static final String dataModel = "a#role_item_catalogue-element-menu-item-link"
    private static final String addImport = "a#add-import-menu-item-link"
    private static final String search = "input#elements"
    private static
    final String table = "#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    private static final String modelHeader = "div.modal-header>h4"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

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

    def "navigate to the top menu and select create relationship"() {
        when: 'navigate to createRelationship page'
        click dataModel
        click addImport

        then: 'verify that the text Destination is displayed'
        check modelHeader displayed
    }

    def "select a data model"() {
        when: 'select a model'
        fill search with "Clinical Tags " and Common.pick first Common.item
        click Common.modalPrimaryButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        select selectModelToEdit

        then: 'verify that  imports is displayed inside table'
        check table contains "imports"
    }
}