package org.modelcatalogue.core.sanityTestSuite

<<<<<<< Updated upstream
/**
 * Created by Berthe on 13/03/2017.
 */
class ClickOnModelCatalogueDevSupportedLinkSpec {
=======
import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Created by Berthe on 13/03/2017.
 */
class ClickOnModelCatalogueDevSupportedLinkSpec extends GebSpec  {
    public static final String model ="div.panel-body>div"
     static WebDriver driver
    void modelDevelopmentLinks(){
        when:
        driver = browser.driver
        driver.manage().deleteAllCookies()
        go("https://gel-mc-test.metadata.org.uk/#/")
        // select the box containing model catalogue development supprot
        WebElement ModelDev= driver.findElement(By.cssSelector(model))
        List<WebElement> Image= ModelDev.findElements(By.tagName("img"))
        // count image in the box
        int countImage = Image.size()
        System.out.println(countImage)
        // loop to get alt of every images
        for(int i= 0 ; i<countImage; i++){
            //Image.get(i).getAttribute("atl")
            System.out.println(Image.get(i).getAttribute("alt"))
        }
        // clicking on every links and get page title
        for(int j =0 ; j <countImage; j++){
            Image.get(j).click()
            System.out.println(driver.getTitle())


        }


        then:
        assert $("button",class:"btn btn-large btn-primary").text()== "Login"
        System.println("I found it")

    }


>>>>>>> Stashed changes
}
