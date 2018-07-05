package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
@Ignore
class RelationshipRelatedToSpec extends AbstractModelCatalogueGebSpec {

    private static final String dataModel = "a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship = "a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String destination = "h3.panel-title"
    private static final String destinationIcon = "span.input-group-addon"
    private static final String relatedTo = "#type > option:nth-child(5)"
    private static final String search = "input#value"
    private static final String removeRelationshipButton = "a#role_item_remove-relationshipBtn"
    private static final String plusButton = "span.fa-plus-square-o"
    private static final String table2 = "tr.inf-table-item-row>td:nth-child(2)"
    private static final String relatedToButton = "ul.nav-tabs>li:nth-child(3)>a"
    private static
    final String table = "#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000

    @Ignore
    def "login to model catalogue and select a data model"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Test 3')
        dashboardPage.select('Test 3')

        then:
        at DataModelPage

        and: 'verify title of the page '
        check rightSideTitle contains 'Test 3'
    }

    @Ignore
    def "navigate to the top menu and select create relationship"() {

        when: 'navigate to createRelationship page'
        click dataModel
        click createRelationship

        then: 'verify that the text Destination is displayed'
        check destination displayed
    }

    @Ignore
    def "select related to,destination and create relationship"() {

        when: 'select relation'
        click relatedTo
        and: ' select destination'
        click destinationIcon
        fill search with '' and pick first item
        click ModalPrimaryButton


        then: 'verify that related to is displayed inside table'
        check table contains "related to"
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
    }

    @Ignore
    def "remove the related to relationship that was created"() {

        when: 'click on the related to tag'
        click relatedToButton

        and: 'select the plus button'
        click plusButton

        and:
        click removeRelationshipButton

        and: 'confirm you action'
        click modalPrimaryButton

        then: 'Check that the first row is gone'
        check table2 isGone()
    }

}
