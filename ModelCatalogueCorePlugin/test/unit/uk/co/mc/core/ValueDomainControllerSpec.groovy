package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller
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
        checkJsonRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1")
    }

    @Unroll
    def "get incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1")
    }


    @Unroll
    def "get outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1")
    }

    @Unroll
    def "get incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1")
    }


    @Unroll
    def "get outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1")
    }

    @Unroll
    def "get incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1")
    }


    Class getResource() { ValueDomain }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        if (!MeasurementUnit.findByName("MPH")) assert (mph = new MeasurementUnit(name: "MPH").save()) else mph = MeasurementUnit.findByName("MPH")
        if (!DataType.findByName("integer")) assert (integer = new DataType(name: "integer").save()) else integer = DataType.findByName("integer")
        [name: "ground_speed_${counter}", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer]
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new ValueDomainMarshaller(), new DataElementMarshaller()]
    }
}


