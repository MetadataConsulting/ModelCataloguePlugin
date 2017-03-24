package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.apache.poi.ss.formula.functions.T
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement


/**
 * Created by Berthe on 14/03/2017.
 */
class MenuBarSpec extends AbstractModelCatalogueGebSpec {

    private static final String searchMenuBar ="a#role_navigation-right_search-menu-menu-item-link>span:nth-child(1)"
    private static final String flash= "span.fa-flash"
    private static final String userMenu ="a#role_navigation-right_user-menu-menu-item-link>span:nth-child(1)"
    private static final String closeSearch= "span.fa-close"
    private static final String searchField="input#value"
    private static final String modelVersion="div.modal-body>div:nth-child(2)>div>a:nth-child(2)"
    private static final String favoriteButton="a#user-favorites-menu-item-link>span:nth-child(3)"
    private static final String apiKey="a#user-api-key-menu-item-link>span:nth-child(3)"
    private static final String regenerateKey="a#user-api-key-menu-item-link>span:nth-child(3)"
    private static final String logOut="a#user-login-right-menu-item-link>span:nth-child(3)"
    private static final String hideButton="button.btn-primary"
    private static final String cancel ="button.btn-warning"

    void verifyMenuBar() {
        when:
        loginCurator()

        then:
        noExceptionThrown()

        when:
        // navigate to search icon
        click searchMenuBar
        // type cancer in the search field and check auto suggestion present
        fill(searchField)with("cancer")
        // wait for 1 min
        Thread.sleep(2000l)
        // close the search
        click closeSearch

        then:
        noExceptionThrown()


        when:
        Thread.sleep(1000l)
        // click on flash icon
        click flash
        // click on model catalogue version
        click(modelVersion)

        then:
        // verify the text present
        assert $("div.modal-header>h4").text()=="Import Model Catalogue XML File"

        when:
        Thread.sleep(1000l)
        //click on hide button
        click hideButton
        Thread.sleep(1000L)
        // CLICK ON USER ICON
        click userMenu
        Thread.sleep(2000L)
        // click on favorite button
        click favoriteButton
        Thread.sleep(1000l)

        then:
        //assert $("tr.inf-table-item-row>td:nth-child(2)>a").size()==1
        noExceptionThrown()

        when:
        // CLICK ON USER ICON
        click userMenu
        Thread.sleep(2000L)
        // navigate API Key and click
        click apiKey
        // wait for one min
        Thread.sleep(1000l)
        // click on o regenerate key Verify the generate number
        find(By.cssSelector(regenerateKey))
        Thread.sleep(1000l)
        // click on cancel
       click cancel
        Thread.sleep(1000l)

        then:
        noExceptionThrown()

        when:
        // CLICK ON USER ICON
        click userMenu
        Thread.sleep(2000L)
        // navigate and click on Log out
        click logOut


       then:
       Thread.sleep(1000l)
       // verify login present
       assert $("button.btn").displayed



    }
}
