package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class RelationshipImportsSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  destinationIcon="span.input-group-addon"
    private static final String   imports ="#type > option:nth-child(7)"
    private static final String   plusButton ="span.fa-plus-square-o"
    private static final String  search ="input#value"
    private static final String  tableImport ="td.col-md-5"
    private static final String  modelCatalogue ="span.mc-name"
    private static final String  removeImportedModel ="a#role_item_remove-relationshipBtn"
    private static final String  table ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"

    def "login to model catalogue and select a data model"() {
        when:
        loginAdmin()
        select'Test 3'

        then:'verify title of the page '
        check rightSideTitle contains 'Test 3'
    }
    def "navigate to the top menu and select create relationship"() {
        when:'navigate to createRelationship page'
        click dataModel
        click createRelationship

        then:'verify that the text Destination is displayed'
        check destination displayed
    }

    def "select imports,destination and create relationship"() {

        when: 'select relation'
        click  imports
        and: ' select destination'
        click destinationIcon
        fill search with "" and pick first item
        click ModalPrimaryButton

        refresh(browser) // TODO: It should not be necessary to refresh the page

        then:'verify that imports is displayed inside table'
        check table contains "imports"
    }

    def "delete the imported data model"() {
        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Imported Data Models'

        and:
        click plusButton

        and:
        click removeImportedModel

        and:
        click modalPrimaryButton

        then:
        check tableImport isGone()
    }
}
