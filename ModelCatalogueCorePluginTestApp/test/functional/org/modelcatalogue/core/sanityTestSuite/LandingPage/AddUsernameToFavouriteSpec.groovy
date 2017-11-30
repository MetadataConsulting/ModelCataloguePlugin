package org.modelcatalogue.core.sanityTestSuite.LandingPage

import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import org.apache.tomcat.jni.Thread
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class AddUsernameToFavouriteSpec extends AbstractModelCatalogueGebSpec {


    private static final String  adminTag = "#role_navigation-right_admin-menu-menu-item-link > span.fa.fa-cog"
    private static final String userName="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td:nth-child(3) > span > span > a"
    private static final String  favourite ="a#role_item-detail_favorite-elementBtn"
    private static final String  User="a#role_navigation-right_user-menu-menu-item-link>span:nth-child(1)"
    private static final String  favouriteTag="a#user-favorites-menu-item-link>span:nth-child(3)"
    private static final String  tableFirstROW ="tr.inf-table-item-row>td:nth-child(1)"
    private static final String   plusButton ="span.fa-plus-square-o"

    def "login to model catalogue"() {
        when:
        loginAdmin()

        then:
        check adminTag displayed
    }

    def "select a data model and navigate to the user profile"() {

    select('Test 3')

    expect:
    check rightSideTitle contains 'Test 3'
    }

    @Ignore
    def "navigate to the author tag and select a username"() {

         when:
         click userName

         then:
         check favourite displayed
    }

    @Ignore
    def "click on the favourite button and verify favourite tag"() {
         when:
         click favourite

         and:
         Thread.sleep(2000l)
         click User
         click favouriteTag

         then:
         check tableFirstROW displayed
    }

    @Ignore
    def "remove the favourite username"() {

        when:
        click plusButton

        and:
        click favourite

        then:
        check tableFirstROW isGone()
    }

}
