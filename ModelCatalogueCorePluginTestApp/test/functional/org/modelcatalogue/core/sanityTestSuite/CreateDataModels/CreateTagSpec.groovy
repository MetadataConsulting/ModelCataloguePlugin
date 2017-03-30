package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.Keys
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class CreateTagSpec extends AbstractModelCatalogueGebSpec{


    def"login to model catalogue and select element"(){

        when:
             loginCurator()
              select 'Test 6' open 'Data Elements' select 'No tags'
        then:
             check rightSideTitle is 'Active Data Elements'
    }

    def"Navigate to create a tag page"(){
        when:
             click create
        then:
            check modalHeader is "Create Data Element"
    }
     def" create a tag"(){
         when:
              fill nameLabel with("this is my tag name ${System.currentTimeMillis()}")
              fill modelCatalogueId with "M-${System.currentTimeMillis()}"
              fill description with "I am creating a tag ${System.currentTimeMillis()}"
              click save

         then:
             noExceptionThrown()


     }
}
