package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class MenuBarSpec extends AbstractModelCatalogueGebSpec {

    private static final String searchMenuBar ="a#role_navigation-right_search-menu-menu-item-link>span:nth-child(1)"
    private static final String flash= "a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String userMenu ="a#role_navigation-right_user-menu-menu-item-link>span:nth-child(1)"
    private static final String OK= "button.btn"
    private static final String searchField="input#value"
    private static final String  activity="div.modal-body>div:nth-child(2)>div>a:nth-child(2)"
    private static final String favoriteButton="a#user-favorites-menu-item-link>span:nth-child(3)"
    private static final String apiKey="a#user-api-key-menu-item-link>span:nth-child(3)"
    private static final String regenerateKey="button.ng-binding"
    private static final String logOut="a#user-login-right-menu-item-link>span:nth-child(3)"
    private static final String  catalogueID="tr.inf-table-header-row>th:nth-child(1)>span"
    private static final String cancel ="button.btn-warning"
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000
    private static final String  activeUsers='div.form-group>table>tbody>tr:nth-child(1)>th'
    private static final String  login ='button.btn'


    def "login to model catalogue"() {
        when:
        loginAdmin()

        then:
        check flash displayed
    }
    def "navigate to the search"() {

        when:
        click searchMenuBar
        fill searchField with "cancer" and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then: 'verify the title'
        check rightSideTitle contains 'Cancer'
    }
     def "select fast action"() {

         when:
         click flash
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
         and: 'select activity'
         click activity
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

         then: 'verify the text present'
         check activeUsers contains 'Most Recent Active Users'
         click OK
     }
     def "navigate to the user tag"() {

         when: 'select user '
         click userMenu
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

         and: 'click on the favourite tag'
         click favoriteButton
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

         then:
         check catalogueID displayed


         when: 'navigate back to the top menu'
         click userMenu
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

         and: 'select api'
         click apiKey
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

         then:
         check modalHeader contains 'API Key'

         when: 'click on regenerate key'

         click regenerateKey
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

         then:
         check modalHeader contains 'API Key'

        when: 'click on the cancel button'
        click cancel
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:'navigate back to the top menu'
        click userMenu
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:'click on the logout button'
        click logOut
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

       then:' verify that the login button is displayed'
       check login displayed


    }
}
