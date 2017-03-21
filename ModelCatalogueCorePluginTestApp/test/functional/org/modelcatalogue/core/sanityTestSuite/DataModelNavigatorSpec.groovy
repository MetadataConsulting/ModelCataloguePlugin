package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver


class DataModelNavigatorSpec extends AbstractModelCatalogueGebSpec {

        private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
        private static final String name="input#name"
        private static final String buttonNext="button#step-next"
        private static final String previousButton="button#step-previous"
                static String modelName= "my test"

        void wizardNavigatorButton(){

            when:
                 // login to model data
                   loginViewer()
            then:
                 noExceptionThrown()

            when:
                   // click on create
                    click create
                   // type a name . please change value
                     fill(name)with(modelName)
                   // navigate next
                    click buttonNext
                    click previousButton

            then:
                 noExceptionThrown()








        }
    }




