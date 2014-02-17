package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.ValueDomainMarshaller

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ValueDomainController)
@Mock([DataElement,ValueDomain, Relationship, RelationshipType, MeasurementUnit, DataType])
class ValueDomainControllerSpec extends AbstractRestfulControllerSpec {

    def author,mph,integer,kph, groundSpeed1, groundSpeed2, groundSpeed3, groundSpeed4, groundSpeed5, groundSpeed6, groundSpeed7, groundSpeed8, groundSpeed9, groundSpeed10, groundSpeed11
    RelationshipType type

    def setup() {
        assert (type = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is related to").save())
        assert (mph = new MeasurementUnit(name: "MPH").save())
        assert (kph = new MeasurementUnit(name: "KPH").save())
        assert (integer = new DataType(name: "integer").save())
        assert (author = new DataElement(name:"Author", description: "the DE_author of the book", code: "XXX").save())
        assert (groundSpeed1 = new ValueDomain(name: "ground_speed", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed2 = new ValueDomain(name: "ground_speed1", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed3 = new ValueDomain(name: "ground_speed2", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed4 = new ValueDomain(name: "ground_speed3", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed5 = new ValueDomain(name: "ground_speed4", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed6 = new ValueDomain(name: "ground_speed5", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed7 = new ValueDomain(name: "ground_speed6", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed8 = new ValueDomain(name: "ground_speed7", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed9 = new ValueDomain(name: "ground_speed8", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed10 = new ValueDomain(name: "ground_speed9", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (groundSpeed11 = new ValueDomain(name: "ground_speed10", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert !Relationship.link(author, groundSpeed1, type).hasErrors()


        def de = new ValueDomainMarshaller()
        de.register()

    }

    def cleanup() {
        author.delete()
        mph.delete()
        integer.delete()
        groundSpeed1.delete()
        groundSpeed2.delete()
        groundSpeed3.delete()
        groundSpeed4.delete()
        groundSpeed5.delete()
        groundSpeed6.delete()
        groundSpeed7.delete()
        groundSpeed8.delete()
        groundSpeed9.delete()
        groundSpeed10.delete()
        groundSpeed11.delete()

        type.delete()
    }


    @Unroll
    def "list items test: #no where max: #max offset: #offset"(){

        when:
        response.format = "json"
        params.max = max
        params.offset = offset

        controller.index()
        def json = response.json


        then:

        json.success
        json.size           == size
        json.total          == total
        json.list
        json.list.size()    == size
        json.next == next
        json.previous == previous



        where:

        no | size | max | offset | total | next                             | previous
        1  | 10   | 10  | 0      | 11    | "/ValueDomain/?max=10&offset=10" | ""
        2  | 5    | 5   | 0      | 11    | "/ValueDomain/?max=5&offset=5"   | ""
        3  | 5    | 5   | 5      | 11    | "/ValueDomain/?max=5&offset=10"  | "/ValueDomain/?max=5&offset=0"
        4  | 3    | 4   | 8      | 11    | ""                               | "/ValueDomain/?max=4&offset=4"
        5  | 1    | 10  | 10     | 11    | ""                               | "/ValueDomain/?max=10&offset=0"
        6  | 2    | 2   | 8      | 11    | "/ValueDomain/?max=2&offset=10"  | "/ValueDomain/?max=2&offset=6"

    }

    def "Show single existing item json"() {
        response.format = "json"

        println(groundSpeed1)
        println(mph)
        println(integer)

        params.id = "${groundSpeed1.id}"

        controller.show()

        def json = response.json

        recordResult 'showOne', json

        expect:
        json
        json.id == groundSpeed1.id
        json.version == groundSpeed1.version
        json.name == groundSpeed1.name
        json.description == groundSpeed1.description
        json.unitOfMeasure.name == groundSpeed1.unitOfMeasure.name
        json.dataType.name == groundSpeed1.dataType.name
        json.incomingRelationships == [count:1, link:"/valueDomain/incoming/${groundSpeed1.id}"]
        json.outgoingRelationships == [count:0, link:"/valueDomain/outgoing/${groundSpeed1.id}"]

    }


    def "Show single existing item xml"() {
        response.format = "xml"

        params.id = "${groundSpeed1.id}"

        controller.show()

        GPathResult xml = response.xml

        expect:
        xml
        xml.@id == groundSpeed1.id
        xml.@version == groundSpeed1.version
        xml.name == groundSpeed1.name
        xml.description == groundSpeed1.description
        xml.unitOfMeasure == groundSpeed1.unitOfMeasure.name
        xml.outgoingRelationships.@link == "/valueDomain/outgoing/${groundSpeed1.id}"
        xml.outgoingRelationships.@count == 0
        xml.incomingRelationships.@link == "/valueDomain/incoming/${groundSpeed1.id}"
        xml.incomingRelationships.@count == 1

    }

    def "Return 404 for non-existing item as JSON"() {
        response.format = "json"

        params.id = "100"

        controller.show()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML"() {
        response.format = "xml"

        params.id = "100"

        controller.show()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }



    def "Create new instance from JSON"() {
        expect:
        !ValueDomain.findByName("air speed")

        when:
        response.format = "json"
        request.json = [name: "air speed", description: "air speed of the plane", unitOfMeasure: [id: 1, name: "MPH"], dataType: [id: 1, name: "integer"]]

        controller.save()

        def created = response.json
        def stored = ValueDomain.findByName("air speed")

        recordResult 'saveOk', created

        then:
        stored
        created
        created.id == stored.id
        created.name == "air speed"
        created.description == "air speed of the plane"
        created.unitOfMeasure.name == "MPH"
        created.dataType.name == "integer"
    }


    def "Do not create new instance from JSON if data are wrong"() {
        expect:
        !ValueDomain.findByName("badElement")

        when:
        response.format = "json"
        request.json = [name: "badElement" , description: "test"]

        controller.save()

        def created = response.json
        def stored = ValueDomain.findByName("badElement")

        recordResult 'saveErrors', created

        then:
        !stored
        created
        created.errors
        created.errors.size() == 1
        created.errors.first().field == 'dataType'
    }

    def "edit instance from JSON"() {
        def instance = ValueDomain.findByName("ground_speed1")

        expect:
        instance


        when:
        def newDescription = "a new description for the ground speed"
        def newUnit = [id: 2, name: "KPH"]
        response.format = "json"
        params.id = instance.id
        request.json = [name:instance.name, description: newDescription,unitOfMeasure: newUnit]

        controller.update()

        def updated = response.json

        recordResult 'saveOk', updated

        then:
        updated
        updated.id == instance.id
        updated.name == instance.name
        updated.description == newDescription
        updated.unitOfMeasure.name == "KPH"
        updated.dataType.name == instance.dataType.name


    }


    def "edit instance with bad JSON"() {
        def instance = ValueDomain.findByName("ground_speed1")

        expect:
        instance

        when:
        response.format = "json"
        params.id = instance.id
        request.json = [name:"g"*256]

        controller.update()

        def updated = response.json

        recordResult 'saveErrors', updated

        then:
        updated
        updated.errors
        updated.errors.size() == 1
        updated.errors.first().field == 'name'


    }



}


