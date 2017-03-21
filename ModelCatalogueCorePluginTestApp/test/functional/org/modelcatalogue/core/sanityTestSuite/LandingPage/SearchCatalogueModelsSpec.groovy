package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

import java.util.concurrent.TimeUnit

/**
 * Created by Berthe on 15/03/2017.
 */
class SearchCatalogueModelsSpec  extends AbstractModelCatalogueGebSpec{
    private static final String searchInput2 ="input[type=text][placeholder='Search Catalogue Models']"
    private static final String defauldButton ="button.btn"
    private static final String  all = "ul.dropdown-menu-right>li:nth-child(1)>a"
    private static final String draft = "ul.dropdown-menu-right>li:nth-child(2)>a"
    private static final String finalized="ul.dropdown-menu-right>li:nth-child(3)>a"

    void doSearch(){
        when:
        loginViewer()

        then:
        noExceptionThrown()

        when:
        // type in the search box
       fill(searchInput2)with("cancel")
        // click on button next to search catalogue
        click defauldButton
        click draft

        then:
        $("button.btn").text()=="Draft"


        when:

       // click on button next to search catalogue
        click defauldButton
        Thread.sleep(1000l)
        click finalized

        then:
        $("button.btn").text()=="Finalized"



    }











}
