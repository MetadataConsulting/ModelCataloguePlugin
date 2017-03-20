package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver


class DataModelNavigatorSpec extends GebSpec {

        private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
        private static final String name="input#name"
        private static final String buttonNext="button#step-next"
        private static final String previousButton="button#step-previous"

        void wizardNavigatorButton(){

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
            find(By.cssSelector(name)).value("My FirstTest")
            Thread.sleep(1000L)
            // navigate next
            find(By.cssSelector(buttonNext)).click()
            Thread.sleep(1000l)
            // navigate back
            find(By.cssSelector(previousButton)).click()


            then:
            noExceptionThrown()
            Thread.sleep(2000l)
            driver.quit()





        }
    }




