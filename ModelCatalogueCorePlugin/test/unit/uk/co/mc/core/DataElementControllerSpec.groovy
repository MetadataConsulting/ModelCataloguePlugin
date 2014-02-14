package uk.co.mc.core

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataElementController)
@Mock([DataElement, Relationship, RelationshipType])
class DataElementControllerSpec extends AbstractRestfulControllerSpec {

    DataElement author
    DataElement title
    RelationshipType type

    def setup() {
        assert (type = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is related to").save())
        assert (author = new DataElement(name:"Author", description: "the DE_author of the book", code: "XXX").save())
        assert (title = new DataElement(name:"Author2", description: "the DE_author of the book", code: "XXX2").save())
        assert !Relationship.link(author, title, type).hasErrors()


        def de = new DataElementMarshaller()
        de.register()

    }

    def cleanup() {
        author.delete()
        title.delete()
        type.delete()
    }


    def "list items"(){

        response.format = "json"

        controller.index()
        def json = response.json


        expect:
        json.success
        json.size           == 2
        json.total          == 2
        json.list
        json.list.size()    == 2
        json.list.any { it.id == author.id }
        json.list.any { it.id == title.id }

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
        json.outgoingRelationships.destinationPath== ["/DataElement/$author.id"]
        json.outgoingRelationships.sourceName == ["$title.name"]
        json.outgoingRelationships.sourcePath == ["/DataElement/$title.id"]
        json.outgoingRelationships.destinationName == ["$author.name"]
        json.outgoingRelationships.relationshipType.sourceClass == ["uk.co.mc.core.CatalogueElement"]
        json.outgoingRelationships.relationshipType.id == [type.id]
        json.outgoingRelationships.relationshipType.sourceToDestination == ["relates to"]
        json.outgoingRelationships.relationshipType.destinationClass == ["uk.co.mc.core.CatalogueElement"]
        json.outgoingRelationships.relationshipType.name == ["relationship"]
        json.outgoingRelationships.relationshipType.getAt("class") == ["uk.co.mc.core.RelationshipType"]
        json.outgoingRelationships.relationshipType.destinationToSource == ["is related to"]



        !json.incomingRelationships

    }

    void "If element not found "()
    {

        expect:
        DataElement.count()==2


        when:
        response.format = "json"
        params.id = 133
        controller.show()

        def result = controller.response

        then:
        result.status == 404

    }


}


