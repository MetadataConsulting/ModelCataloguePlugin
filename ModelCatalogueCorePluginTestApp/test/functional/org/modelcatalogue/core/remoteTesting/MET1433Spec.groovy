package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class MET1433Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static
    final String versionsTreeViews = 'ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(10)>div>span>span'
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String first_row = 'tr.inf-table-item-row>td:nth-child(1)'
    private static final String data_row = 'tr.inf-table-item-row'

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

    def "Navigate and Select Versions"() {
        when: "Select a Versions"
        selectInTree 'Versions'

        then:
        check Common.rightSideTitle contains 'Cancer Model History'
    }

    def "Check the version of all element"() {
        when:
        click versionsTreeViews

        then:
        check first_row contains '0.0.1'
    }
}