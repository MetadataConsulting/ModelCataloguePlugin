package org.modelcatalogue.core.sanityTestSuite.LandingPage

import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class AddDataModelImportSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String addImport ="a#add-import-menu-item-link"
    private static final String  search ="input#elements"
    private static final String  table ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    private static final String  modelHeader="div.modal-header>h4"
    private static final String  plusButton="span.fa-plus-square-o"
    private static final String  removeButton="a#role_item_remove-relationshipBtn"
    private static final String  tableImported ="td.col-md-5"
    private static final String  modelCatalogue ="span.mc-name"

    def "login to model catalogue and select a data model"() {

        when:
        loginAdmin()
        Thread.sleep(4000l)
        select 'Test 3'

        then:'verify title of the page '
        check rightSideTitle contains 'Test 3'
    }
    def "navigate to the top menu and select create relationship"() {
        when:'navigate to createRelationship page'
        click dataModel
        click addImport

        then:'verify that the text Destination is displayed'
        check  modelHeader displayed

    }
    def "select a data model"() {
        when: 'select a model'
        fill search with "cancer" and pick first item
        click ModalPrimaryButton
        refresh(browser) // TODO: It should not be necessary to refresh the page

        then: 'verify that  imports is displayed inside table'
        check table contains "imports"
    }

    def "delete the imported data model"() {

        when:'navigate back to the main page'
        click modelCatalogue
        and:
        select 'Test 3'
        selectInTree 'Imported Data Models'

        then:'verify the title'
        check rightSideTitle contains 'Test 3 Imports'

        when:'click on the plus button'
        click plusButton

        and:'remove the imported data models'
        click removeButton

        and:'click on the ok button'
        click modalPrimaryButton

        then:'verify that imported is removed'
        check tableImported gone
    }
}
