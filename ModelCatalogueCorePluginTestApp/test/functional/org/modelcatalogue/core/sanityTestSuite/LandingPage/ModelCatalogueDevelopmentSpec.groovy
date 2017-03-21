package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Created by Berthe on 17/03/2017.
 */
class ModelCatalogueDevelopmentSpec extends AbstractModelCatalogueGebSpec {

    private static final String modelDevBox="footer.row>div>div>div"

    void clickOnDevelopmentSupported(){

        when:
        // enter username , password
        loginCurator()

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
