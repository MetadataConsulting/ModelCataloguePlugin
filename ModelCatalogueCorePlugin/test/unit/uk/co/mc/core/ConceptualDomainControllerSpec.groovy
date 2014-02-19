package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.ConceptualDomainMarshaller
import uk.co.mc.core.util.marshalling.DataElementMarshaller
import uk.co.mc.core.util.marshalling.ModelMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ConceptualDomainController)
@Mock([ConceptualDomain, Relationship, RelationshipType, Model])
class ConceptualDomainControllerSpec extends AbstractRestfulControllerSpec {

    Model book
    RelationshipType type

    def setup() {
        new ConceptualDomainMarshaller().register()
        fixturesLoader.load('conceptualDomains/CD_publicLibraries', 'models/M_book', 'relationshipTypes/RT_relationship', 'conceptualDomains/CD_universityLibraries')

        assert (loadItem1 = fixturesLoader.CD_publicLibraries.save())
        assert (book = fixturesLoader.M_book.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(loadItem1, book, type).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.CD_universityLibraries)
        assert (badInstance = new ConceptualDomain(name: "", description:"asdf"))
        assert (propertiesToEdit = [description: "edited description "])
        assert (propertiesToCheck = ['name','description'])

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

    def cleanup() {
        type.delete()
        book.delete()
    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Concept${counter}", description: "the conceptual domain"]
    }

    Class getResource() {
        ConceptualDomain
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new ConceptualDomainMarshaller(), new DataElementMarshaller(), new ModelMarshaller()]
    }
}


