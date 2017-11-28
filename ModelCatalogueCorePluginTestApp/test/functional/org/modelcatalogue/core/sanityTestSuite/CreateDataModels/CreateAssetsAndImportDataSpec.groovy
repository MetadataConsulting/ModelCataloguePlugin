package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.AssetWizardSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

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

    def"login and navigate to the model"(){
        when:
        loginCurator()
        select 'Test 3'
        selectInTree 'Assets'

        then:
        check rightSideTitle contains 'Active Assets'
    }
    def"navigate to create asset page"(){
        when:
        click create
        then:
        check modalHeader contains 'Create Asset'
    }
    def"create a new asset"(){
        when:
        fill nameLabel with " Sample excel${System.currentTimeMillis()}"
        fill asset with file ('example.xml')
        fill description with 'This is my asset'
        click save

        then:
        check table displayed
    }
    def"delete the created asset"(){

        when: 'click on the model catalogue to return home'
        click modelCatalogue

        and: 'select test 3 again'
        select 'Test 3'
        selectInTree 'Assets'

        then:
        check rightSideTitle contains 'Active Assets'

        when: 'select the created asset'
        click createdAsset

        and: 'navigate to the top menu'
        click assetButton

        and: 'scroll down the list and click on the delete button'
        click deleteButton

        and: 'click on the OK button to confirm deletion'
        click modalPrimaryButton

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check table gone

    }
    def"login and navigate to model"(){
        when:
        loginCurator()
        select 'Test 3'
        selectInTree 'Imported Data Models'
        then:
        check rightSideTitle contains 'Imports'
    }

    def" import model "(){
        when:
        addDataModelImport 'Clinical Tags'
        then:
        check importedDataModel contains 'Clinical Tags'
    }
    def"remove the imported data model "(){

        when:
        click modelCatalogue

        and: ' select test3 and navigate to import tag'
        select 'Test 3'
        selectInTree 'Imported Data Models'
        then:
        check rightSideTitle contains 'Imports'

        when:'click on the plus button'
        click plusButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:'select the remove button'
        click removeButton

        and:'confirm your action'
        click modalPrimaryButton

        then:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        check importedDataModel gone

    }
    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }

}
