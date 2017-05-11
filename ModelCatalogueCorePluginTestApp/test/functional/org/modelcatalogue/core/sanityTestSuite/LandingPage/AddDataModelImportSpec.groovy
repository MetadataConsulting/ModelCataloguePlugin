package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle


@Stepwise
class AddDataModelImportSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String addImport ="a#add-import-menu-item-link"
    private static final String  search ="input#elements"
    private static final String  table ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    public static final String  modelHeader="div.modal-header>h4"


    def "login to model catalogue and select a data model"(){

        when:
        loginAdmin()
        select'TEST 7'

        then:'verify title of the page '
        check rightSideTitle contains 'TEST 7'
    }
    def"navigate to the top menu and select create relationship "(){

        when:'navigate to createRelationship page'
        click dataModel
        click addImport

        then:'verify that the text Destination is displayed'
        check  modelHeader displayed

    }
    def"select a data model "(){

        when: 'select a model'
        fill search with "s" and pick first item
        click ModalPrimaryButton

        then:'verify that  imports is displayed inside table'
        check table contains "imports"


    }
}
