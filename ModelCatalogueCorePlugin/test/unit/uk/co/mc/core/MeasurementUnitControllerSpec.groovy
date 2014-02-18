package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.MeasurementUnitMarshallers

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(MeasurementUnitController)
@Mock([MeasurementUnit, Relationship, RelationshipType])
class MeasurementUnitControllerSpec extends AbstractRestfulControllerSpec {

    RelationshipType relationshipType

    def setup() {
        fixturesLoader.load('measurementUnits/MU_degree_C', 'measurementUnits/MU_milesPerHour', 'measurementUnits/MU_degree_F', 'relationshipTypes/RT_relationship')
        new MeasurementUnitMarshallers().register()

        assert (relationshipType = fixturesLoader.RT_relationship.save())
        assert (loadItem1 = fixturesLoader.MU_degree_C.save())
        assert (loadItem2 = fixturesLoader.MU_degree_F.save())
        assert !Relationship.link(loadItem1, loadItem2, relationshipType).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.MU_milesPerHour)
        assert (badInstance = new MeasurementUnit(name: "", symbol: "km"))
        assert (propertiesToEdit = [symbol: "_C_"])
        assert (propertiesToCheck = ['name','description', 'symbol'])
    }

    def cleanup() {
        loadItem1.delete()
        loadItem2.delete()
        relationshipType.delete()
    }


    def "get outgoing relationships"() {
        params.id = loadItem1.id
        response.format = "json"

    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Measurement Unit ${counter}", symbol: "MU${counter}"]
    }

    Class getResource() {
        MeasurementUnit
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new MeasurementUnitMarshallers()]
    }
}
