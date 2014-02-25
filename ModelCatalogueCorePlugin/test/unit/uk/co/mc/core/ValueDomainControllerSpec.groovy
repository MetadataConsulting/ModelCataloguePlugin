package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller
import uk.co.mc.core.util.marshalling.MappingsMarshaller
import uk.co.mc.core.util.marshalling.MeasurementUnitMarshallers
import uk.co.mc.core.util.marshalling.ValueDomainMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ValueDomainController)
@Mock([DataElement, ValueDomain, Relationship, RelationshipType, MeasurementUnit, DataType, Mapping])
class ValueDomainControllerSpec extends AbstractRestfulControllerSpec {

    def author, mph, integer, kph
    RelationshipType type

    def setup() {
        fixturesLoader.load('measurementUnits/MU_kph', 'dataElements/DE_author', 'measurementUnits/MU_milesPerHour', 'dataTypes/DT_integer', 'relationshipTypes/RT_relationship')

        new ValueDomainMarshaller().register()
        assert (type = fixturesLoader.RT_relationship.save())
        assert (mph = fixturesLoader.MU_milesPerHour.save())
        assert (kph = fixturesLoader.MU_kph.save())
        assert (integer = fixturesLoader.DT_integer.save())
        assert (author = fixturesLoader.DE_author.save())
        assert (loadItem1 = new ValueDomain(name: "ground_speed", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (loadItem2 = new ValueDomain(name: "ground_speed5", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert !Relationship.link(loadItem1, author, type).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = new ValueDomain(name: "ground_speed2", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer))
        assert (badInstance = new ValueDomain(name: "", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer))
        assert (propertiesToEdit = [description: "something different"])
        assert (propertiesToCheck = ['name', 'description', 'unitOfMeasure.name', 'dataType.@id'])

    }

    def cleanup() {
        author?.delete()
        mph?.delete()
        integer?.delete()
        type?.delete()
    }


    Class getResource() { ValueDomain }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        if (!MeasurementUnit.findByName("MPH")) assert (mph = new MeasurementUnit(name: "MPH").save()) else mph = MeasurementUnit.findByName("MPH")
        if (!DataType.findByName("integer")) assert (integer = new DataType(name: "integer").save()) else integer = DataType.findByName("integer")
        [name: "ground_speed_${counter}", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer]
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new ValueDomainMarshaller(), new DataElementMarshaller(), new MappingsMarshaller(), new MeasurementUnitMarshallers()]
    }

    @Unroll
    def "get json mapping: #no where max: #max offset: #offset\""() {
        fillWithDummyEntities(15)
        ValueDomain first = ValueDomain.get(1)
        mapToDummyEntities(first)

        when:
        params.id = first.id
        params.offset = offset
        params.max = max
        response.format = "json"
        controller.mappings(max)
        def json = response.json

        recordResult"mapping$no", json

        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)

        when:
        def item  = json.list[0]
        def mapping = first.outgoingMappings.find {it.id == item.id}

        then:
        item.mapping == mapping.mapping
        item.destination
        item.destination.id == mapping.destination.id
        item.destination.elementType
        item.destination.elementType == mapping.destination.class.name

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/mapping/1")
    }


    @Unroll
    def "get xml mapping: #no where max: #max offset: #offset\""() {
        fillWithDummyEntities(15)
        ValueDomain first = ValueDomain.get(1)
        mapToDummyEntities(first)

        when:
        params.id = first.id
        params.offset = offset
        params.max = max
        response.format = "xml"
        controller.mappings(max)
        def xml = response.xml

        recordResult"mapping$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.mapping.size() == size


        when:
        def item  = xml.mapping[0]
        def mapping = first.outgoingMappings.find {it.id == item.@id.text() as Long}

        then:
        item.mapping.text() == mapping.mapping
        item.destination
        item.destination.@id.text() == "$mapping.destination.id"
        item.destination.@elementType
        item.destination.@elementType.text() == mapping.destination.class.name

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/mapping/1")
    }

    protected mapToDummyEntities(ValueDomain toBeLinked) {
        for (domain in resource.list()) {
            if (domain != toBeLinked) {
                Mapping.map(toBeLinked, domain, "x")
                if (toBeLinked.outgoingMappings.size() == 12) {
                    break
                }
            }
        }

        assert toBeLinked.outgoingMappings
        assert toBeLinked.outgoingMappings.size() == 12
        toBeLinked
    }


    // -- begin copy and pasted

    @Unroll
    def "get json outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1")
    }

    @Unroll
    def "get json incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1")
    }


    @Unroll
    def "get json outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1/relationship")
    }

    @Unroll
    def "get json incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1/relationship")
    }


    @Unroll
    def "get json outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1/xyz")
    }

    @Unroll
    def "get json incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1/xyz")
    }

    @Unroll
    def "get xml outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1")
    }

    @Unroll
    def "get xml incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1")
    }


    @Unroll
    def "get xml outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1/relationship")
    }

    @Unroll
    def "get xml incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1/relationship")
    }


    @Unroll
    def "get xml outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/outgoing/1/xyz")
    }

    @Unroll
    def "get xml incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/incoming/1/xyz")
    }

    // -- end copy and pasted
}


