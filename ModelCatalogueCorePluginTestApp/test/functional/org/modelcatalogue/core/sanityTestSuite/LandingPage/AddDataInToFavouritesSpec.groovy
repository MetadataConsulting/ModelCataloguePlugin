package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver


class AddDataInToFavouritesSpec extends GebSpec {

    private static final String cancerModelsSelector = "full-width-link ng-binding"
    private static final String showMore="div.content-row>div>div:nth-child(2)>div:nth-child(1)>div>div:nth-child(2)>div>div:nth-child(4)>div>div>div:nth-child(2)>div:nth-child(1)>div:nth-child(2)>form>div:nth-child(5)>p>span"

    void addToFavorite(){
        when:
        WebDriver driver = browser.driver
        go(baseUrl)
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
        assert $("label",for:"username").text()=="Username or Email"

        when:
        // enter username , password and check remember me
        $("input#username").value("viewer")
        $("input#password").value("viewer")
        $("div.checkbox>label>input").value(true)
        // click on login
        $("button.btn-success").click()
        Thread.sleep(1000L)

        then:
        noExceptionThrown()

        when:
        find(By.cssSelector(showMore)).click()
        Thread.sleep(1000l)
       // $("a",id:"role_item-infinite-list_favorite-elementBtn").click()

       then:
       noExceptionThrown()
        Thread.sleep(10000L)
       driver.quit()

    }

  }


