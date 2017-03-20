package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Created by Berthe on 17/03/2017.
 */
class ModelCatalogueDevelopmentSpec extends GebSpec {
    private static final String modelDevBox="footer.row>div>div>div"

    void clickOnDevelopmentSupported(){

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
        // select model catalogue development supported
        WebElement DEV =driver.findElement(By.cssSelector(modelDevBox))
        // wait for one min
        Thread.sleep(1000L)
        // select all the image on the box
         List<WebElement> LIST= DEV.findElements(By.xpath("//a[@class='text-muted']"))
        Thread.sleep(1000L)
        // click on all the image
        for(int i = 0;i< LIST.size();i++){

            System.out.println(LIST.get(i).getAttribute("href"))
            LIST.get(i).click()


        }
        then:
       assert $("footer.row>div>div>div>div>div:nth-child(3)>p:nth-child(1)>a>img").size()==1
        Thread.sleep(10000L)
        driver.quit()
    }

}
