package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
@Stepwise
class RelationshipIsSynonymForSpec extends AbstractModelCatalogueGebSpec {

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  plusButton="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.col-md-1 > a > span"
    private static final String  undo="#role_item_undo-changeBtn"
    private static final String   isSynonymFor ="#type > option:nth-child(6)"
    private static final String  search ="input#element"
    private static final String  table ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000

    def "login to model catalogue and select a data model"() {
        when:
        loginAdmin()
        select'Test 3'

        then:'verify title of the page '
        check rightSideTitle contains 'Test 3'
    }

    def "navigate to the top menu and select create relationship "() {

        when:'navigate to createRelationship page'
        click dataModel
        and:
        click createRelationship

        then:'verify that the text Destination is displayed'
        check destination displayed
    }

    def "select is synonym for, destination and create relationship"() {
        when: 'select relation'
        click isSynonymFor

        and: ' select destination'
        fill search with "MET-523" and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and: 'click on the create button'
        click ModalPrimaryButton

        then: 'verify that is synonym for is displayed inside table'
        check table contains "is synonym for"
    }

    def "remove the is synonym for that was created"() {
        when: 'click on the plus button to expand the file'
        click plusButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:'click on the undo button'
        click undo

        and:' confirm your action'
        click modalPrimaryButton

        and:
        click plusButton

        then:'check that undo button is disabled'
        check undo isDisabled()
    }
}
