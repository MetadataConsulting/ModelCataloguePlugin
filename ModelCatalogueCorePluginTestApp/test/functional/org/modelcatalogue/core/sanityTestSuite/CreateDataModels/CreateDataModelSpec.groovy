package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement


import java.security.Key

/**
 * Created by Berthe on 17/03/2017.
 */
class CreateDataModelSpec extends GebSpec {
    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String version="input#semanticVersion"
    private static final String catalogueId="input#modelCatalogueId"
    private static final String policies="input#dataModelPolicy"
    private static final String description="textarea#description"
    private static final String importData="input#name"
    private static final String finish="button#step-finish"


    void doCreateDataModel(){

        when:
        WebDriver driver = browser.driver
        go(baseUrl)
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
        assert $("label",for:"username").text()=="Username or Email"

        when:
        // enter username , password and check remember me . please notice you have to change the login detail
        $("input#username").value("viewer")
        $("input#password").value("viewer")
        $("div.checkbox>label>input").click()
        // click on login
        $("button.btn-success").click()
        Thread.sleep(1000L)

        then:
        noExceptionThrown()

        when:
        // click on create
        find(By.cssSelector(create)).click()
        // wait for one min
        Thread.sleep(1000L)
        // type a name . please change value
        find(By.cssSelector(name)).value("My First Test2")
        Thread.sleep(1000L)
        // type a version
        find(By.cssSelector(version)).value("2.1.29")
        Thread.sleep(1000L)
        // type a id
        find(By.cssSelector(catalogueId)).value("33")
        Thread.sleep(1000L)
        // enter policies
        $(By.cssSelector(policies)).value("my policies as a tester find bug")
        // switch to import
        driver.findElement(By.cssSelector(policies)).sendKeys(Keys.ENTER)
        Thread.sleep(1000l)
        then:
        // get title
        System.out.println(driver.getTitle())
        //assert title =="Data Model"

        when:
        // type c in the search box . change value if needed
        $(By.cssSelector(importData)).value("c")
        Thread.sleep(1000L)
        // list of models . speak to berthe if finding any issue
        List < WebElement> models= driver.findElements(By.xpath("//li"))
        // size of models
        int count = models.size()
        // print size of models
        System.out.println(count)
        // select element in index 2 . mean the third element on drop down list  .feel free to change
        for(int i =0; i< count; i++){
            models.get(2).click()
        }

        Thread.sleep(5000l)
        // click on finish
        $(By.cssSelector(finish)).click()


       then:
       // please this unique for this data . change string data for your own data or use noexcep
      assert $("strong",class:"ng-binding").text()=="My First Test2 created"
       // click on close
       Thread.sleep(5000L)
       $("button", cloass:"btn btn-default").click()

       //noExceptionThrown()
        Thread.sleep(10000L)
        driver.quit()
    }
}
