package uk.co.mc.core.fixtures

import spock.lang.Specification
import uk.co.mc.core.DataType
import uk.co.mc.core.MeasurementUnit

/**
 * Created by ladin on 17.02.14.
 */
class MockFixturesLoaderScriptSpec extends Specification {


    def "Script collects fixtures in named map"() {
        MockScript script = new MockScript()
        script.setLoader(new MockFixturesLoader())
        script.run()

        expect:
        script.fixtures
        script.fixtures.DT_double
        script.fixtures.DT_double instanceof DataType
        script.fixtures.DT_double.name == 'double'
        script.fixtures.DT_string
        script.fixtures.DT_string instanceof DataType
        script.fixtures.DT_string.name == 'string'
        script.fixtures.MU_degree_C
        script.fixtures.MU_degree_C instanceof MeasurementUnit
        script.fixtures.MU_degree_C.name == "Degrees of Celsius"

    }

}

class MockScript extends MockFixturesLoaderScript {

    Object run() {

        load "measurementUnits/MU_degree_C"

        fixture {
            DT_double(DataType, name: 'double')
            DT_string(DataType, name: 'string')

            println DT_double
        }
    }
}
