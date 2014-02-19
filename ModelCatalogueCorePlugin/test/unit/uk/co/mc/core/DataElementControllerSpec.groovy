package uk.co.mc.core

import grails.converters.JSON
import grails.converters.XML
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

    RelationshipType type

    def setup() {

        fixturesLoader.load('dataElements/DE_author', 'dataElements/DE_author1', 'dataElements/DE_author2',  'relationshipTypes/RT_relationship')

        assert (loadItem1 = fixturesLoader.DE_author.save())
        assert (loadItem2 = fixturesLoader.DE_author1.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(loadItem1, loadItem2, type).hasErrors()

        loadItem1.ext.foo = "bar"

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.DE_author2)
        assert (badInstance = new DataElement(name: "", description:"asdf"))
        assert (propertiesToEdit = [description: "edited description ", code: "AA123"])
        assert (propertiesToCheck = ['name','description', 'code', '@status', '@versionNumber'])


    }

    def cleanup() {
        type.delete()
    }

//    def "Show single existing item"() {
//        response.format = "json"
//
//        params.id = "${loadItem1.id}"
//
//        controller.show()
//
//        def json = response.json
//
//        recordResult 'showOne', json
//
//        expect:
//        json
//        json.id == loadItem1.id
//        json.version == loadItem1.version
//        json.name == loadItem1.name
//        json.description == loadItem1.description
//        json.code == loadItem1.code
//        json.ext == [foo: 'bar']
//        json.outgoingRelationships == [count: 1, link: "/dataElement/outgoing/${loadItem1.id}"]
//        json.incomingRelationships == [count: 0, link: "/dataElement/incoming/${loadItem1.id}"]
//
//    }
//
//
//    def "Show single existing item xml"() {
//        response.format = "xml"
//
//        params.id = "${loadItem1.id}"
//
//        controller.show()
//
//        GPathResult xml = response.xml
//
//        recordResult("showOne", xml)
//
//        expect:
//        xml
//        xml.@id == loadItem1.id
//        xml.@version == loadItem1.version
//        xml.name == loadItem1.name
//        xml.description == loadItem1.description
//        xml.code == loadItem1.code
//        xml.extensions.extension[0].@key == "foo"
//        xml.extensions.extension[0].text() == "bar"
//        xml.outgoingRelationships.@link == "/dataElement/outgoing/${loadItem1.id}"
//        xml.outgoingRelationships.@count == 1
//        xml.incomingRelationships.@link == "/dataElement/incoming/${loadItem1.id}"
//        xml.incomingRelationships.@count == 0
//
//
//    }
//
//    def "Do not create new instance from bad XML"() {
//        expect:
//        !DataElement.findByName("")
//
//        when:
//        response.format = "xml"
//        def xml = new DataElement(name: "", description: "a new data element", code: "x").encodeAsXML()
//        request.xml = xml
//
//        controller.save()
//
//        GPathResult created = response.xml
//        def stored = DataElement.findByName("")
//
//        recordResult 'saveErrors', created
//
//        then:
//        !stored
//        created
//        created == "Property [name] of class [class uk.co.mc.core.DataElement] cannot be null"
//    }
//
//    def "edit instance from JSON"() {
//        def instance = resource.findByName(loadItem2.name)
//
//        expect:
//        instance
//
//        when:
//        def newDescription = "a new description for the book"
//        response.format = "json"
//        params.id = instance.id
//        request.json = [name: instance.name, description: newDescription]
//
//        controller.update()
//
//        def updated = response.json
//
//        recordResult 'updateOk', updated
//
//        then:
//        updated
//        updated.id == instance.id
//        updated.name == instance.name
//        updated.description == newDescription
//        updated.code == instance.code
//        updated.status == instance.status.toString()
//
//
//    }
//
//    def "edit instance from XML"() {
//        def instance = DataElement.findByName(loadItem2.name)
//
//        expect:
//        instance
//
//        when:
//        def newDescription = "a new description for the book"
//        response.format = "xml"
//        params.id = instance.id
//        request.xml = new DataElement(name: instance.name, description: newDescription, code: instance.code).encodeAsXML()
//
//        controller.update()
//
//        GPathResult updated = response.xml
//
//        recordResult 'updateOk', updated
//
//        then:
//        updated
//        updated.@id == instance.id
//        updated.@version == instance.version
//        updated.name == instance.name
//        updated.description == newDescription
//        updated.code == instance.code
//        updated.@status == instance.status
//        updated.@versionNumber == instance.versionNumber
//
//
//    }
//
//    def "edit instance with bad XML"() {
//        def instance = DataElement.findByName(loadItem2.name)
//
//        expect:
//        instance
//
//        when:
//        response.format = "xml"
//        params.id = instance.id
//        request.xml = new DataElement(name: "", description: "blah blah blah", code: instance.code).encodeAsXML()
//
//        controller.update()
//
//        GPathResult updated = response.xml
//
//        recordResult 'updateErrors', updated
//
//        then:
//        updated
//        updated== "Property [name] of class [class uk.co.mc.core.DataElement] cannot be null"
//
//    }


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


