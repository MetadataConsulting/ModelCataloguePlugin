package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
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

    DataElement author, author1
    RelationshipType type

    def setup() {
        assert (type = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is related to").save())
        assert (author = new DataElement(name: "Author", description: "the DE_author of the book", code: "XXX").save())
        assert (author1 = new DataElement(name: "Author1", description: "the DE_author of the book", code: "XXX21").save())
        assert !Relationship.link(author, author1, type).hasErrors()

        author.ext.foo = "bar"

    }

    def cleanup() {
        type.delete()
    }

    def "Show single existing item"() {
        response.format = "json"

        params.id = "${author.id}"

        controller.show()

        def json = response.json

        recordResult 'showOne', json

        expect:
        json
        json.id == author.id
        json.version == author.version
        json.name == author.name
        json.description == author.description
        json.code == author.code
        json.ext == [foo: 'bar']
        json.outgoingRelationships == [count: 1, link: "/dataElement/outgoing/${author.id}"]
        json.incomingRelationships == [count: 0, link: "/dataElement/incoming/${author.id}"]

    }


    def "Show single existing item xml"() {
        response.format = "xml"

        params.id = "${author.id}"

        controller.show()

        GPathResult xml = response.xml

        recordResult("show-single", xml)

        expect:
        xml
        xml.@id == author.id
        xml.@version == author.version
        xml.name == author.name
        xml.description == author.description
        xml.code == author.code
        xml.extensions.size() == 1
        xml.extensions.extension[0].@key == "foo"
        xml.extensions.extension[0].text() == "bar"

        xml.outgoingRelationships.@link == "/dataElement/outgoing/${author.id}"
        xml.outgoingRelationships.@count == 1
        xml.incomingRelationships.@link == "/dataElement/incoming/${author.id}"
        xml.incomingRelationships.@count == 0

    }


    def "Create new instance from JSON"() {
        expect:
        !DataElement.findByName("C13052")

        when:
        response.format = "json"
        request.json = [name: "C13052", description: "a new data element", code: "NHIC123"]

        controller.save()

        def created = response.json
        def stored = DataElement.findByName("C13052")

        recordResult 'saveOk', created

        then:
        stored
        created
        created.id == stored.id
        created.name == "C13052"
        created.description == "a new data element"
        created.code == "NHIC123"
    }


    def "Do not create new instance from JSON if data are wrong"() {
        expect:
        !DataElement.findByName("badElement")

        when:
        response.format = "json"
        request.json = [name: "badElement", description: "test", code: "x" * 256]

        controller.save()

        def created = response.json
        def stored = DataElement.findByName("badElement")

        recordResult 'saveErrors', created

        then:
        !stored
        created
        created.errors
        created.errors.size() == 1
        created.errors.first().field == 'code'
    }

    def "edit instance from JSON"() {
        def instance = DataElement.findByName("Author")

        expect:
        instance


        when:
        def newDescription = "a new description for the book"
        response.format = "json"
        params.id = instance.id
        request.json = [name: instance.name, description: newDescription]

        controller.update()

        def updated = response.json

        recordResult 'updateOk', updated

        then:
        updated
        updated.id == instance.id
        updated.name == instance.name
        updated.description == newDescription
        updated.code == instance.code
        updated.status == instance.status.toString()


    }

    def "edit instance with bad JSON"() {
        def instance = DataElement.findByName("Author")

        expect:
        instance

        when:
        response.format = "json"
        params.id = instance.id
        request.json = [name: "g" * 256]

        controller.update()

        def updated = response.json

        recordResult 'updateErrors', updated

        then:
        updated
        updated.errors
        updated.errors.size() == 1
        updated.errors.first().field == 'name'


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
        [name: "Author${counter}", description: "the DE_author of the book", code: "XXX${counter}"]
    }

    Class getResource() {
        DataElement
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new DataElementMarshaller()]
    }
}


