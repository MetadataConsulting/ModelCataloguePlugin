package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class AddUsernameToFavouriteSpec extends AbstractModelCatalogueGebSpec {
    private static final String  adminTag = "#role_navigation-right_admin-menu-menu-item-link > span.fa.fa-cog.fa-fw.fa-2x-if-wide.ct-active"

    def "login to model catalogue"(){

        when:
        loginAdmin()

        then:
        check adminTag displayed
    }



}
