package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.json.JSONElement
import spock.lang.Unroll
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
        assert (propertiesToCheck = ['name', 'symbol'])
    }

    def cleanup() {
        loadItem1.delete()
        loadItem2.delete()
        relationshipType.delete()
    }


    @Unroll
    def "get outgoing relationships pagination: #no where max: #max offset: #offset"() {
        fixturesLoader.load('relationshipTypes/RT_relationship')
        RelationshipType relationshipType = fixturesLoader.RT_relationship.save() ?: RelationshipType.findByName('relationship')
        fillWithDummyEntities(15)

        expect:
        relationshipType

        when:

        def first = resource.get(1)

        first.outgoingRelationships = first.outgoingRelationships ?: []

        for (unit in resource.list()) {
            if (unit != first) {
                assert !Relationship.link(first, unit, relationshipType).hasErrors()
                if (first.outgoingRelationships.size() == 12) {
                    break
                }
            }
        }

        then:
        first.outgoingRelationships
        first.outgoingRelationships.size() == 12

        when:
        response.format = "json"
        params.offset = offset
        params.id = first.id

        controller.outgoing(max)
        JSONElement json = response.json


        recordResult "outgoing${no}", json

        then:

        json.success
        json.total == total
        json.size == size
        json.list
        json.list.size() == size
        json.next == next
        json.previous == previous

        cleanup:
        relationshipType?.delete()

        where:
        no | size | max | offset | total | next                                           | previous
        1  | 10   | 10  | 0      | 12    | "/${resourceName}/outgoing/1?max=10&offset=10" | ""
        2  | 5    | 5   | 0      | 12    | "/${resourceName}/outgoing/1?max=5&offset=5"   | ""
        3  | 5    | 5   | 5      | 12    | "/${resourceName}/outgoing/1?max=5&offset=10"  | "/${resourceName}/outgoing/1?max=5&offset=0"
        4  | 4    | 4   | 8      | 12    | ""                                             | "/${resourceName}/outgoing/1?max=4&offset=4"
        5  | 2    | 10  | 10     | 12    | ""                                             | "/${resourceName}/outgoing/1?max=10&offset=0"
        6  | 2    | 2   | 10     | 12    | ""                                             | "/${resourceName}/outgoing/1?max=2&offset=8"
    }

    @Unroll
    def "get incoming relationships pagination: #no where max: #max offset: #offset"() {
        fixturesLoader.load('relationshipTypes/RT_relationship')
        RelationshipType relationshipType = fixturesLoader.RT_relationship.save() ?: RelationshipType.findByName('relationship')
        fillWithDummyEntities(15)

        expect:
        relationshipType

        when:
        def first = resource.get(1)
        first.incomingRelationships = first.incomingRelationships ?: []

        for (unit in resource.list()) {
            if (unit != first) {
                assert !Relationship.link(unit, first, relationshipType).hasErrors()
                if (first.incomingRelationships.size() == 12) {
                    break
                }
            }
        }

        then:
        first.incomingRelationships
        first.incomingRelationships.size() == 12

        when:
        response.format = "json"
        params.offset = offset
        params.id = first.id

        controller.incoming(max)
        JSONElement json = response.json


        recordResult "incoming${no}", json

        then:

        json.success
        json.total == total
        json.size == size
        json.list
        json.list.size() == size
        json.next == next
        json.previous == previous

        cleanup:
        relationshipType?.delete()

        where:
        no | size | max | offset | total | next                                           | previous
        1  | 10   | 10  | 0      | 12    | "/${resourceName}/incoming/1?max=10&offset=10" | ""
        2  | 5    | 5   | 0      | 12    | "/${resourceName}/incoming/1?max=5&offset=5"   | ""
        3  | 5    | 5   | 5      | 12    | "/${resourceName}/incoming/1?max=5&offset=10"  | "/${resourceName}/incoming/1?max=5&offset=0"
        4  | 4    | 4   | 8      | 12    | ""                                             | "/${resourceName}/incoming/1?max=4&offset=4"
        5  | 2    | 10  | 10     | 12    | ""                                             | "/${resourceName}/incoming/1?max=10&offset=0"
        6  | 2    | 2   | 10     | 12    | ""                                             | "/${resourceName}/incoming/1?max=2&offset=8"
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
