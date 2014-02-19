package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.json.JSONElement
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataElementController)
@Mock([DataElement, Relationship, RelationshipType, ExtensionValue])
class DataElementControllerSpec extends AbstractRestfulControllerSpec {

    RelationshipType type

    def setup() {

        fixturesLoader.load('dataElements/DE_author', 'dataElements/DE_author1', 'dataElements/DE_author2', 'relationshipTypes/RT_relationship')

        assert (loadItem1 = fixturesLoader.DE_author.save())
        assert (loadItem2 = fixturesLoader.DE_author1.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(loadItem1, loadItem2, type).hasErrors()

        loadItem1.ext.foo = "bar"

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.DE_author2)
        assert (badInstance = new DataElement(name: "", description: "asdf"))
        assert (propertiesToEdit = [description: "edited description ", code: "AA123"])
        assert (propertiesToCheck = ['name', 'description', 'code', '@status', '@versionNumber'])


    }

    def cleanup() {
        type.delete()
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
        [name: "loadItem2${counter}", description: "the DE_author of the book", code: "XXX${counter}"]
    }

    Class getResource() {
        DataElement
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new DataElementMarshaller()]
    }
}


