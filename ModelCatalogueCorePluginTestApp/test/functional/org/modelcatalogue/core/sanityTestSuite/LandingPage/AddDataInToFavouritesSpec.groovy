package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.admin
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class AddDataInToFavouritesSpec extends AbstractModelCatalogueGebSpec {
    private static final String  creates  = "a#role_data-models_create-data-modelBtn"
    private static final String  user="#role_navigation-right_user-menu-menu-item-link"
    private static final String  favouriteButton= "a#user-favorites-menu-item-link"
    private static final String   catalogueID = "tr.inf-table-header-row>th:nth-child(1)"
    private static final String    searchField= "input#value"
    private static final String   firstRow ="tr.inf-table-item-row>td:nth-child(1)"
    private static final String   removeFavourite ="span.fa-star-o"
    private static final String   favouriteModel ="tr.inf-table-item-row>td:nth-child(2)>a"
    public static final String  greenButton ="#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > ui-view > div > div > div > div.inf-table-body > table > tfoot > tr > td > table > tfoot > tr > td.text-center > span"

    def "login to model catalogue"() {
        when:
        login admin

        then:
        check creates displayed
    }

    def "navigate to favourites"() {
        when:
        click user
        click favouriteButton

        then:
        check catalogueID contains "Model Catalogue ID"
    }

     def "add data to favourite"() {
         when:
         click greenButton

         then:
         check modalHeader is "Add to Favourites"

         when:
         fill searchField with 'Ovarian Cancer (NHIC 0.0.1)' and pick first item
         click modalPrimaryButton

         refresh(browser) // TODO: It should not be necessary to refresh the page

         then:
         check firstRow displayed
     }

    @Ignore
    def "remove favourite data model"() {
        when:
        click favouriteModel

        then:
        check rightSideTitle contains 'Cancer'

        when:
        click removeFavourite

        and:
        click user
        click favouriteButton

        then:
        check firstRow isGone()
    }
}
