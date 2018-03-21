package org.modelcatalogue.core.remoteTesting

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class MET1566Spec extends AbstractModelCatalogueGebSpec {
    private static final String modelHeaderName = 'h3.ce-name'
    private static final myModel = "#my-models"
    private static
    final String addItemIcon = "div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle"

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

    @Unroll("Going to Test  Add #sno for #treeItem")
    def "Navigate and Select #treeItem"() {
        when:
        selectInTree treeItem

        then:
        check Common.rightSideTitle contains "Active $treeItem"
        $(addItemIcon).displayed == false
        where:
        sno | treeItem
        1   | "Data Classes"
        2   | "Data Elements"
        3   | "Data Types"
        4   | "Measurement Units"
        5   | "Business Rules"
        6   | "Assets"
        7   | "Tags"

    }
}