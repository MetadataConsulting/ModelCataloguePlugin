package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class CreateMeasurementUnitSpec extends AbstractModelCatalogueGebSpec{
    private static final String name="input#name"
    private static final String symbol="input#symbol"




    def"login to model catalogue and navigate to the model"(){
        when:
              loginCurator()
              select'TEST5'
              selectInTree 'Measurement Units'
        then:
              check rightSideTitle contains 'Active Measurement Units'
    }
    def" Navigate to measure unit page"(){

        when:
              click create

        then:
             check modalHeader  contains "Create Measurement Unit"
    }
    def" create Measurement unit"(){
        when:
             fill name with("My measurement${System.currentTimeSeconds()}")
             fill symbol with("kilogram")
             fill modelCatalogueId with("METT-${System.currentTimeSeconds()}")
             fill description with(" this is my measurement ${System.currentTimeSeconds()}")
             click save

        then:
             noExceptionThrown()


    }


}
