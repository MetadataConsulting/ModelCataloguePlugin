package org.modelcatalogue.core.sanityTestSuite.LandingPage

import com.mysql.jdbc.NotImplemented
import geb.spock.GebSpec
import groovy.transform.NotYetImplemented
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver


class AddDataInToFavouritesSpec extends AbstractModelCatalogueGebSpec {

    private static final String cancerModelsSelector = "full-width-link ng-binding"
    private static final String showMore="div.content-row>div>div:nth-child(2)>div:nth-child(1)>div>div:nth-child(2)>div>div:nth-child(4)>div>div>div:nth-child(2)>div:nth-child(1)>div:nth-child(2)>form>div:nth-child(5)>p>span"
    @NotYetImplemented
    void addToFavorite(){

        when:
        loginViewer()
        then:
        noExceptionThrown()

        when:
        click showMore

        Thread.sleep(1000l)
       // $("a",id:"role_item-infinite-list_favorite-elementBtn").click()

       then:
       noExceptionThrown()
        Thread.sleep(10000L)
       driver.quit()

    }

  }


