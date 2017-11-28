package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class QuickSearchAsViewerSpec extends AbstractModelCatalogueGebSpec{

     private static final String catalogueModels = "#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > div:nth-child(1) > div > div:nth-child(2) > div > ul > li:nth-child(2) > a"
     private static final String  quickSearch='a#role_navigation-right_search-menu-menu-item-link>span:nth-child(1)'
     private static final String   search  ='input#value'

    def "login to model catalogue"() {

        loginCurator()

        expect:
        check catalogueModels contains 'Catalogue Models'
    }

    def "navigate to the top menu and select quick search"() {

        when:
        click quickSearch
        Thread.sleep(1000)

        and:'search for an element'
        fill search with 'Clinical trial' and pick first item
        Thread.sleep(1000)

        then:
        check rightSideTitle contains 'Clinical'

    }
}
