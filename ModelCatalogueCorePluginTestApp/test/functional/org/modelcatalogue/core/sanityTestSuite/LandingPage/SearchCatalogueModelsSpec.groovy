package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

import java.util.concurrent.TimeUnit

/**
 * Created by Berthe on 15/03/2017.
 */
class SearchCatalogueModelsSpec  extends GebSpec{
    private static final String searchInput ="input.form-control"
    private static final String defauldButton ="button.btn-default"
    private static final String  all = "ul.dropdown-menu-right>li:nth-child(1)>a"
    private static final String draft = "ul.dropdown-menu-right>li:nth-child(2)>a"
    private static final String finalized="ul.dropdown-menu-right>li:nth-child(3)>a"

    void doSearch(){
        when:
        // navigate to model catalogue
        WebDriver driver = browser.driver
        // wait for 60 min for an element to display
        driver.manage().timeouts().implicitlyWait(60,TimeUnit.SECONDS)
        // wait for 10 min for page to load
        driver.manage().timeouts().pageLoadTimeout(10,TimeUnit.MINUTES)
        // maximize the window when launch
        driver.manage().window().maximize()
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
        // click on login
        $("button.btn-success").click()

        then:
        noExceptionThrown()

        when:
        Thread.sleep(1000L)
        // type in the search box
        find(By.cssSelector(searchInput)).value("cancer")
        Thread.sleep(1000l)
        // click on button next to search catalogue
        driver.findElement(By.cssSelector(defauldButton)).click()
        Thread.sleep(1000l)
        // click on draft
        driver.findElement(By.cssSelector(draft)).click()

        then:
       noExceptionThrown()

        when:
        Thread.sleep(1000l)
        // click on button next to search catalogue
        driver.findElement(By.cssSelector(defauldButton)).click()
        Thread.sleep(1000l)
        find(By.cssSelector(finalized)).click()

        then:
        noExceptionThrown()
        Thread.sleep(1000l)
       driver.close()
    }











}
