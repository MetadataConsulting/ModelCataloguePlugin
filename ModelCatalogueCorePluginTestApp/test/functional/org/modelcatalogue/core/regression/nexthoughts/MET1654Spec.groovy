package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
//@Ignore
class MET1654Spec extends AbstractModelCatalogueGebSpec {
    private static final String myModel = "#my-models"
    static
    final String addItemIcon = "div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle"

    def "Login to Model Catalouge"() {

        when: "Login using Curator Account"
        loginCurator()

        then: "My Modal Should be displayed"
        check myModel displayed
    }

    def "Select Admin Data Model"() {
        when:
        select 'Cancer Model'
        selectInTree 'Data Types'

        then:
        check Common.rightSideTitle contains 'Active Data Types'
        $(addItemIcon).displayed == false
    }
}