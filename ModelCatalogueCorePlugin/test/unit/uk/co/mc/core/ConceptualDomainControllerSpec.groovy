package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
import uk.co.mc.core.util.marshalling.ConceptualDomainMarshaller
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ConceptualDomainController)
@Mock([ConceptualDomain, Relationship, RelationshipType, Model])
class ConceptualDomainControllerSpec extends AbstractRestfulControllerSpec {

    ConceptualDomain publicLibs
    Model book
    RelationshipType type

    def setup() {
        fixturesLoader.load('conceptualDomains/CD_publicLibraries', 'models/M_book', 'relationshipTypes/RT_relationship')

        assert (publicLibs = fixturesLoader.CD_publicLibraries.save())
        assert (book = fixturesLoader.M_book.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(publicLibs, book, type).hasErrors()

        def de = new ConceptualDomainMarshaller()
        de.register()

    }

    def cleanup() {
        type.delete()
        book.delete()
    }

    def "Show single existing item"() {
        response.format = "json"

        params.id = "${publicLibs.id}"

        controller.show()

        def json = response.json

        recordResult 'showOne', json

        expect:
        json
        json.id == publicLibs.id
        json.version == publicLibs.version
        json.name == publicLibs.name
        json.description == publicLibs.description
        json.outgoingRelationships == [count: 1, link: "/conceptualDomain/outgoing/${publicLibs.id}"]
        json.incomingRelationships == [count:0, link:"/conceptualDomain/incoming/${publicLibs.id}"]

    }


    def "Show single existing item xml"() {
        response.format = "xml"

        params.id = "${publicLibs.id}"

        controller.show()

        GPathResult xml = response.xml

        expect:
        xml
        xml.@id == publicLibs.id
        xml.@version == publicLibs.version
        xml.name == publicLibs.name
        xml.description == publicLibs.description
        xml.outgoingRelationships.@link == "/conceptualDomain/outgoing/${publicLibs.id}"
        xml.outgoingRelationships.@count == 1
        xml.incomingRelationships.@link == "/conceptualDomain/incoming/${publicLibs.id}"
        xml.incomingRelationships.@count == 0

    }



    def "Create new instance from JSON"() {
        expect:
        !ConceptualDomain.findByName("school libraries")

        when:
        response.format = "json"
        request.json = [name: "school libraries", description: "a new conceptual domain"]

        controller.save()

        def created = response.json
        def stored = ConceptualDomain.findByName("school libraries")

        recordResult 'saveOk', created

        then:
        stored
        created
        created.id == stored.id
        created.name == "school libraries"
        created.description == "a new conceptual domain"
    }


    def "Do not create new instance from JSON if data are wrong"() {
        expect:
        !ConceptualDomain.findByName("badElement")

        when:
        response.format = "json"
        request.json = [name: "c"*256 , description: "test" ]

        controller.save()

        def created = response.json
        def stored = ConceptualDomain.findByName("c"*256)

        recordResult 'saveErrors', created

        then:
        !stored
        created
        created.errors
        created.errors.size() == 1
        created.errors.first().field == 'name'
    }

    def "edit instance from JSON"() {
        def instance = ConceptualDomain.findByName("public libraries")

        expect:
        instance


        when:
        def newDescription = "a new description for the cd"
        response.format = "json"
        params.id = instance.id
        request.json = [name:instance.name, description: newDescription]

        controller.update()

        def updated = response.json

        recordResult 'saveOk', updated

        then:
        updated
        updated.id == instance.id
        updated.name == instance.name
        updated.description == newDescription


    }

    def "edit instance with bad JSON"() {
        def instance = ConceptualDomain.findByName("public libraries")

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

    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Concept${counter}", description: "the conceptual domain"]
    }

    Class getResource() {
        ConceptualDomain
    }

}


