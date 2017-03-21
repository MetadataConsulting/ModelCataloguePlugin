package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import static org.modelcatalogue.core.geb.Common.*


import java.security.Key

/**
 * Created by Berthe on 17/03/2017.
 */
class CreateDataModelSpec extends AbstractModelCatalogueGebSpec {
    private static final String create = "a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name = "input#name"
    private static final String version = "input#semanticVersion"
    private static final String catalogueId = "input#modelCatalogueId"
    private static final String policies = "input#dataModelPolicy"
    private static final String description = "textarea#description"
    private static final String importData = "input#name"
    private static final String finish = "button#step-finish"

    def "do create data model"() {

        when:
        // enter username , password
        loginCurator()

        then:
        noExceptionThrown()

        when:
        // click on create
        click create

        then:
        // wait for one min
        Thread.sleep(1000L)
        // type a name . please change value
         fill(name)with(newModelName)
        Thread.sleep(1000L)
        // type a version
        fill(version)with(versionElement)
        Thread.sleep(1000L)
        // type a catalogueid
        fill(catalogueId)with(catalogue)
        Thread.sleep(1000L)
        // enter policies
        fill(policies)with(policy)
        // switch to import
        find(By.cssSelector(policies))<<Keys.ENTER
        Thread.sleep(1000l)


        then:
        // verify import button present
       assert $("button#step-imports").text()== "2. Imports"
        //assert title =="Data Model"

        when:
        // type c in the search box . change value if needed
        fill(importData)with(text)
        Thread.sleep(1000L)
        // list of models . speak to berthe if finding any issue
        List<WebElement> models = driver.findElements(By.xpath("//li"))
        // size of models
        int count = models.size()
        // print size of models
        System.out.println(count)
        // select element in index 2 . mean the third element on drop down list  .feel free to change
        for (int i = 0; i < count; i++) {
            models.get(2).click()
        }

        Thread.sleep(5000l)
        // click on finish
        click finish


        then:
        Thread.sleep(5000l)
        // please this unique for this data . change string data for your own data or use noexcep
        assert $("strong", class: "ng-binding").text() == newModelName + " created"

        cleanup:
        // click on close
        Thread.sleep(5000L)
        click modalCloseButton


        where:
        newModelName|versionElement| catalogue | policy|text
        "my model 3"  |"2.1.28"      | "MT-33"   | " FIND BUG"| "C"



    }
}
