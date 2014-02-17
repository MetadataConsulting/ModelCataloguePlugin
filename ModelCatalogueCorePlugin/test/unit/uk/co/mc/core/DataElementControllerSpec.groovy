package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataElementController)
@Mock([DataElement, Relationship, RelationshipType])
class DataElementControllerSpec extends AbstractRestfulControllerSpec {

    DataElement author, author1
    RelationshipType type

    def setup() {
        assert (type = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is related to").save())
        assert (author = new DataElement(name:"Author", description: "the DE_author of the book", code: "XXX").save())
        assert (author1 = new DataElement(name:"Author1", description: "the DE_author of the book", code: "XXX21").save())
        assert !Relationship.link(author, author1, type).hasErrors()

        new DataElementMarshaller().register()
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
        json.name == author.name
        json.outgoingRelationships == [count: 1, link: "/dataElement/outgoing/${author.id}"]
        json.incomingRelationships == [count:0, link:"/dataElement/incoming/${author.id}"]

    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Author${counter}", description: "the DE_author of the book", code: "XXX${counter}"]
    }

    Class getResource() {
        DataElement
    }

}


