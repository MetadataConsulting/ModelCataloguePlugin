package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
@Ignore
class MET1630Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
// private static
//     final String catalogueModels = "#metadataCurator>div.container-fluid.container-main>div>div>div.ng-scope>div:nth-child(1)>div>div:nth-child(2)>div>ul>li:nth-child(2)>a"
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 10000L

    def "Login to Model Catalouge"() {

        when: "Login using Curator Account"
        login curator

        then: "My Modal Should be displayed"
        check myModel displayed
    }

    def "Select Admin Data Model"() {
        when:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        select 'Test 1'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        selectInTree 'Data Types'

        then:
        check Common.rightSideTitle contains 'Active Data Types'
    }

    def "navigate to data type creation page "() {
        when:
        click Common.create

        then:
        check Common.modalHeader contains 'Create Data Type'
    }

    def "fill the create data type form"() {
        when:
        println("////////////1/////////////")
        fill Common.nameLabel with dataTypeName
        println("////////////2/////////////")
        fill Common.description with "my description of data type"
        println("////////////4/////////////")
        click Common.save
        println("////////////8/////////////")

        then:
        check Common.modalHeader contains 'Login'
    }
}