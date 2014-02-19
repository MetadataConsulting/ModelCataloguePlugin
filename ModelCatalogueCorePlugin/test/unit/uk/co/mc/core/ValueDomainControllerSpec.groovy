package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.json.JSONElement
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.ValueDomainMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ValueDomainController)
@Mock([DataElement, ValueDomain, Relationship, RelationshipType, MeasurementUnit, DataType])
class ValueDomainControllerSpec extends AbstractRestfulControllerSpec {

    def author, mph, integer, kph
    RelationshipType type

    def setup() {
        fixturesLoader.load('measurementUnits/MU_kph','dataElements/DE_author', 'measurementUnits/MU_milesPerHour','dataTypes/DT_integer', 'relationshipTypes/RT_relationship')

        new ValueDomainMarshaller().register()
        assert (type = fixturesLoader.RT_relationship.save())
        assert (mph = fixturesLoader.MU_milesPerHour.save())
        assert (kph = fixturesLoader.MU_kph.save())
        assert (integer = fixturesLoader.DT_integer.save())
        assert (author = fixturesLoader.DE_author.save())
        assert (loadItem1 = new ValueDomain(name: "ground_speed", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert !Relationship.link(loadItem1, author, type).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = new ValueDomain(name: "ground_speed2", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer))
        assert (badInstance = new ValueDomain(name: "", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer))
        assert (propertiesToEdit = [description: "something different"])
        assert (propertiesToCheck = ['name','description', 'unitOfMeasure.name', 'dataType.@id'])

    }

    def cleanup() {
        author?.delete()
        mph?.delete()
        integer?.delete()
        type?.delete()
    }

    @Unroll
    def "get outgoing relationships pagination: #no where max: #max offset: #offset"() {
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


    Class getResource() { ValueDomain }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        if (!MeasurementUnit.findByName("MPH")) assert (mph = new MeasurementUnit(name: "MPH").save()) else mph = MeasurementUnit.findByName("MPH")
        if (!DataType.findByName("integer")) assert (integer = new DataType(name: "integer").save()) else integer = DataType.findByName("integer")
        [name: "ground_speed_${counter}", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer]
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new ValueDomainMarshaller()]
    }
}


