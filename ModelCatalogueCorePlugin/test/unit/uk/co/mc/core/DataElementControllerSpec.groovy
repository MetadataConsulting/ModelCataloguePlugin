package uk.co.mc.core

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataElementController)
@Mock([DataElement, Relationship, RelationshipType])
class DataElementControllerSpec extends AbstractRestfulControllerSpec {

    DataElement author, author1, author2, author3, author4, author5, author6, author7, author8, author9, author10, author11
    RelationshipType type

    def setup() {
        assert (type = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is related to").save())
        assert (author = new DataElement(name:"Author", description: "the DE_author of the book", code: "XXX").save())
        assert (author1 = new DataElement(name:"Author1", description: "the DE_author of the book", code: "XXX21").save())
        assert (author2 = new DataElement(name:"Author2", description: "the DE_author of the book", code: "XXX22").save())
        assert (author3 = new DataElement(name:"Author3", description: "the DE_author of the book", code: "XXX23").save())
        assert (author4 = new DataElement(name:"Author4", description: "the DE_author of the book", code: "XXX24").save())
        assert (author5 = new DataElement(name:"Author5", description: "the DE_author of the book", code: "XXX25").save())
        assert (author6 = new DataElement(name:"Author6", description: "the DE_author of the book", code: "XXX26").save())
        assert (author7 = new DataElement(name:"Author7", description: "the DE_author of the book", code: "XXX27").save())
        assert (author8 = new DataElement(name:"Author8", description: "the DE_author of the book", code: "XXX28").save())
        assert (author9 = new DataElement(name:"Author9", description: "the DE_author of the book", code: "XXX29").save())
        assert (author10 = new DataElement(name:"Author10", description: "the DE_author of the book", code: "XXX92").save())
        assert (author11 = new DataElement(name:"Author11", description: "the DE_author of the book", code: "XXX210").save())
        assert !Relationship.link(author, author1, type).hasErrors()


        def de = new DataElementMarshaller()
        de.register()

    }

    def cleanup() {
        author.delete()
        author1.delete()
        author2.delete()
        author3.delete()
        author4.delete()
        author5.delete()
        author6.delete()
        author7.delete()
        author8.delete()
        author9.delete()
        author10.delete()
        author11.delete()
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
        1  | 10   | 10  | 0      | 12    | "/DataElement/?max=10&offset=10" | "null"
        2  | 5    | 5   | 0      | 12    | "/DataElement/?max=5&offset=5"   | "null"
        3  | 5    | 5   | 5      | 12    | "/DataElement/?max=5&offset=10"  | "/DataElement/?max=5&offset=0"
        4  | 4    | 4   | 8      | 12    | "/DataElement/?max=4&offset=8"   | "/DataElement/?max=4&offset=4"
        5  | 2    | 10  | 10     | 12    | "/DataElement/?max=10&offset=10" | "/DataElement/?max=10&offset=0"
        6  | 2    | 2   | 10     | 12    | "/DataElement/?max=10&offset=10" | "/DataElement/?max=10&offset=0"

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
        json.name == author.name
        json.outgoingRelationships == [count: 1, link: "/dataElement/outgoing/${author.id}"]
        json.incomingRelationships == [count:0, link:"/dataElement/incoming/${author.id}"]

    }

    void "If element not found "()
    {

        expect:
        DataElement.count()==12


        when:
        response.format = "json"
        params.id = 133
        controller.show()

        def result = controller.response

        then:
        result.status == 404

    }


}


