package org.modelcatalogue.core.regression.nexthoughts

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.Ignore
import groovy.io.FileType

@Stepwise
@Ignore
class MET1561Spec extends AbstractModelCatalogueGebSpec {
    private static final myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String exportButton = "a#role_item_export-menu-item-link"
    private static final String catalogueExportButton = "a#catalogue-element-export-specific-reports_12-menu-item-link"
    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')

    def "Login to Model Catalogue"() {

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
        when:
        File file = new File("/home/vijay/")
        new File("/home/vijay/Cancer_Model.mc.xml").delete()

        click exportButton
        click catalogueExportButton
        Thread.sleep(5000)
        List<String> files = []
        file.eachFile(FileType.FILES) {
            println(it.name)
            files.add(it.name)
        }

        then:
        files.contains("Cancer_Model.mc.xml")
    }
}