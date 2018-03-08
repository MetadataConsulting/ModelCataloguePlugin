package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
//@Ignore
class MET1561Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String exportButton = "a#role_item_export-menu-item-link"
    private static final String catalougeExportButton = "a#catalogue-element-export-specific-reports_4-menu-item-link"
    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')

    def "Login to Model Catalouge"() {

        when: "Login using Curator Account"
        login curator

        then: "My Modal Should be displayed"
        check myModel displayed
    }

    def "Select a finalized Data Model"() {
        when: "Selected an Finalized Data Model"
        select 'Cancer Model'

        then: "Data Model Page Should Open"
        check modelHeaderName displayed
        check modelHeaderName contains 'Cancer Model'
    }

    def "Click on the export button"() {
        click exportAction
    }

}