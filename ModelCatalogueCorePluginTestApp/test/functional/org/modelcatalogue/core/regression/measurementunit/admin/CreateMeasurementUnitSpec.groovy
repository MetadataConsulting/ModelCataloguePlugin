package org.modelcatalogue.core.regression.measurementunit.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class CreateMeasurementUnitSpec extends AbstractModelCatalogueGebSpec{
    private static final String name="input#name"
    private static final String symbol="input#symbol"
    private static final String  table="td.col-md-2"
    private static final String  deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static final String  measurementUnit="td.col-md-4>a"
    private static final String  measurementUnitButton="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"

    def "login to model catalogue and navigate to the model"() {
        when:
              loginCurator()
              select'Test 3'
              selectInTree 'Measurement Units'
        then:
              check Common.rightSideTitle contains 'Active Measurement Units'
    }
    def "Navigate to measure unit page"() {

        when:
              click Common.create

        then:
             check Common.modalHeader  contains "Create Measurement Unit"
    }
    def "create Measurement unit"() {
        when:
             fill name with("TESTING_KILOGRAM")
             fill symbol with("kilogram")
             fill Common.modelCatalogueId with("METT-1000")
             fill Common.description with(" this is my measurement ${System.currentTimeSeconds()}")
             click Common.save

        then:
            check table contains 'kilogram'
    }

    def "remove the created measurement"() {
        when:'click on the created  measurement unit'
        click measurementUnit

        and:'navigate to the top menu and click on the measurement unit button'
        click measurementUnitButton

        and:
        click deleteButton

        and:'confirmation'
        click Common.modalPrimaryButton

        then:
        check table gone
    }
}
