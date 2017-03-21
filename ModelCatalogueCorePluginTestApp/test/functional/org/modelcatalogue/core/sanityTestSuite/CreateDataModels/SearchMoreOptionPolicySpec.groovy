package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.repack.org.scribe.builder.api.FacebookApi
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

import static org.modelcatalogue.core.geb.Common.getModalCloseButton
import static org.modelcatalogue.core.geb.Common.messages

/**
 * Created by Berthe on 17/03/2017.
 */
class SearchMoreOptionPolicySpec extends AbstractModelCatalogueGebSpec{
    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String policies="input#dataModelPolicy"
    private static final String searchMore="a.show-more-cep-item"
    private static final String  searchForData="input#value"

    def" search option"(){

        when:
        // enter username , password
        loginCurator()
        remove messages

        then:
        noExceptionThrown()

        when:
        // click on create
         click create
        // wait for one min
        Thread.sleep(1000L)
        // type a name . please change value
       fill(name)with(myname)
        Thread.sleep(1000L)
        // enter policies
        fill(policies)with(policyText)
        // select search more
        Thread.sleep(2000l)
       click searchMore
        Thread.sleep(5000L)
        fill(searchForData)with(searchData)
        Thread.sleep(1000L)
        find(By.cssSelector(searchForData))<< Keys.ENTER

        then:
        Thread.sleep(10000L)
        noExceptionThrown()
        click modalCloseButton

        where:
        myname |policyText | searchData
        "first test" |"tester find bug" |"My new rule"



    }

}
