package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.IgnoreIf
import spock.lang.Stepwise




@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class SearchCatalogueModelsSpec  extends AbstractModelCatalogueGebSpec{
    private static final String searchInput2 ="#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > div:nth-child(1) > div > div:nth-child(1) > div > div > div > div > input"
    private static final String defaultButton = 'button.ng-binding'
    private static final String all = "ul.dropdown-menu-right>li:nth-child(1)>a"
    private static final String draft = "ul.dropdown-menu-right>li:nth-child(2)>a"
    private static final String finalized = "ul.dropdown-menu-right>li:nth-child(3)>a"
    private static final String  catalogueModels ='ul.nav-tabs>li:nth-child(2)>a'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

    def " login to model catalogue"() {
        when:
        loginCurator()
        and:'click on the catalogue model'
        click catalogueModels
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check catalogueModels isDisplayed()
    }
    def"search for a draft model"() {

        when: 'type in the search box'
        fill searchInput2 with "cancel"

        and: 'click on button next to search catalogue'
        click defaultButton
        click draft

        then: 'verify that draft is displayed'
        check defaultButton contains  "Draft"
    }
     def "search for finalized model"(){

         when: 'click on button next to search catalogue'
         click defaultButton
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
         click finalized

         then:
         check defaultButton is "Finalized"

     }


}
