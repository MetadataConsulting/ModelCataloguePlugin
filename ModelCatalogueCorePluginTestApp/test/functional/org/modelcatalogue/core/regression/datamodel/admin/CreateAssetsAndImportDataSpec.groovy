package org.modelcatalogue.core.regression.datamodel.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.rightSideTitle

import org.modelcatalogue.core.regression.asset.admin.AssetWizardSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
@Stepwise
class CreateAssetsAndImportDataSpec extends AbstractModelCatalogueGebSpec{
    private static final String asset ="input#asset"
    private static final String  modelCatalogue = "span.mc-name"
    private static final String  createdAsset = "a.preserve-new-lines"
    private static final String  assetButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  deleteButton = "a#delete-menu-item-link>span:nth-child(3)"
    private static final String  table = "tr.inf-table-item-row>td:nth-child(2)"
    private static final String  plusButton= "span.fa-plus-square-o"
    private static final String  removeButton= "a#role_item_remove-relationshipBtn"
    private static final String  importedDataModel= "td.col-md-5"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000


    def "login and navigate to model"() {
        when:
        loginCurator()
        select 'Test 3'
        selectInTree 'Imported Data Models'
        then:
        check Common.rightSideTitle contains 'Imports'
    }

    def "MET-1626: import model "() {
        when:
        addDataModelImport 'Clinical Tags'

        then:
        check importedDataModel contains 'Clinical Tags'
    }

    def "MET-1620: remove the imported data model"() {
        when:
        click modelCatalogue

        and: ' select test3 and navigate to import tag'
        select 'Test 3'
        selectInTree 'Imported Data Models'

        then:
        check Common.rightSideTitle contains 'Imports'

        when:'click on the plus button'
        click plusButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:'select the remove button'
        click removeButton

        and:'confirm your action'
        click Common.modalPrimaryButton

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check importedDataModel gone
    }

    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }
}
