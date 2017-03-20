package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.apache.poi.ss.formula.functions.T
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement


/**
 * Created by Berthe on 14/03/2017.
 */
class MenuBarSpec extends GebSpec  {

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

    void verifyMenuBar() {
        when:
        WebDriver driver = browser.driver
        go(baseUrl)
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
        assert $("label",for:"username").text()=="Username or Email"

        when:
        // enter username , password
        $("input#username").value("viewer")
        $("input#password").value("viewer")
       // $("div.checkbox>label>input").click()
        // click on login
        $("button.btn-success").click()
        Thread.sleep(1000L)

        then:
        noExceptionThrown()

        when:
        // navigate to search icon
        find(By.cssSelector(searchMenuBar)).click()
        // type cancer in the search field and check auto suggestion present
        find(By.cssSelector(searchField)).value("cancer")
        // wait for 1 min
        Thread.sleep(2000l)
        // close the search
        find(By.cssSelector(closeSearch)).click()

        then:
        noExceptionThrown()


        when:
        Thread.sleep(1000l)
        // click on flash icon
        find(By.cssSelector(flash)).click()
        // click on model catalogue version
        find(By.cssSelector(modelVersion)).click()

        then:
        // verify the text present
        assert $("div.modal-header>h4").text()=="Model Catalogue Version"

        when:
        Thread.sleep(1000l)
        //click on hide button
       $("button.btn-primary") .click()
        Thread.sleep(1000L)
        // CLICK ON USER ICON
        find(By.cssSelector(userMenu)).click()
        Thread.sleep(2000L)
        // click on favorite button
       WebElement user= driver.findElement(By.cssSelector(favoriteButton))
        user.click()
        Thread.sleep(1000l)

        then:
        assert $("tr.inf-table-item-row>td:nth-child(2)>a").text()== "Rare Diseases"
        noExceptionThrown()

        when:
        // CLICK ON USER ICON
        find(By.cssSelector(userMenu)).click()
        Thread.sleep(2000L)
        // navigate API Key and click
        find(By.cssSelector(apiKey)).click()
        // wait for one min
        Thread.sleep(1000l)
        // click on o regenerate key Verify the generate number
        find(By.cssSelector(regenerateKey))
        Thread.sleep(1000l)
        // click on cancel
        $("button.btn-warning").click()
        Thread.sleep(1000l)

        then:
        noExceptionThrown()

        when:
        // CLICK ON USER ICON
        find(By.cssSelector(userMenu)).click()
        Thread.sleep(2000L)
        // navigate and click on Log out
        find(By.cssSelector(logOut)).click()

       then:
       Thread.sleep(1000l)
       // verify login present
       assert $("button.btn").displayed
       // close the windows
       driver.close()



    }
}
