package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.EnumeratedTypeMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(EnumeratedTypeController)
@Mock([EnumeratedType, Relationship, RelationshipType])
class EnumeratedTypeControllerSpec extends AbstractRestfulControllerSpec {

    RelationshipType type

    def setup() {
        new EnumeratedTypeMarshaller().register()
        fixturesLoader.load('enumeratedTypes/ET_schoolSubjects', 'enumeratedTypes/ET_uniSubjects', 'enumeratedTypes/ET_uni2Subjects', 'relationshipTypes/RT_relationship')

        assert (loadItem1 = fixturesLoader.ET_schoolSubjects.save())
        assert (loadItem2 = fixturesLoader.ET_uniSubjects.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(loadItem1, loadItem2, type).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = new EnumeratedType(name: "sub4", enumerations: [h: 'history', p: 'politics', sci: 'science']))
        assert (badInstance = new EnumeratedType(name: "", description: "asdf"))
        assert (propertiesToEdit = [description: "edited description ", enumerations: ["T1": 'test1', "T2": 'test2', "T3": 'test3']])
        assert (propertiesToCheck = ['name', 'description', 'enumerations'])

    }

    def cleanup() {
        type.delete()
    }

    //override the xml check with extra check for enumerations
    //FIX ME this is a bit of a hack but it provably isn't worth the time
    //to create a specific check for arbitrary attributes within the property
    //you are checking for

    @Override
    boolean xmlPropertyCheck(xml, loadItem) {


        def xmlProp = (xml["name"].toString()) ?: null

        if (xmlProp != loadItem.getProperty("name")) {
            throw new AssertionError("error: property to check: name  where xml:${xml["name"]} !=  item:${loadItem.getProperty("name")}")
        }

        xmlProp = (xml["description"].toString()) ?: null

        if (xmlProp != loadItem.getProperty("description")) {
            throw new AssertionError("error: property to check: description  where xml:${xml["description"]} !=  item:${loadItem.getProperty("description")}")
        }

        xmlProp = xml.depthFirst().find { it.name() == "enumerations" }
        if (xmlProp) {
            xmlProp = xmlProp.attributes()
            if (xmlProp != loadItem.getProperty("enumerations")) {
                throw new AssertionError("error: property to check: enumeration  where xml:${xmlProp} !=  item:${loadItem.getProperty("enumerations")}")

            }
        }

        return true


    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "ENumeratedType${counter}", enumerations: ['H': 'history', 'P': 'politics', 'SCI': 'science', 'GEO': 'geography']]
    }

    Class getResource() {
        EnumeratedType
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new EnumeratedTypeMarshaller()]
    }

    // -- begin copy and pasted

    @Unroll
    def "get json outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing")
    }

    @Unroll
    def "get json incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming")
    }


    @Unroll
    def "get json outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/relationship")
    }

    @Unroll
    def "get json incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/relationship")
    }


    @Unroll
    def "get json outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/xyz")
    }

    @Unroll
    def "get json incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/xyz")
    }

    @Unroll
    def "get xml outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing")
    }

    @Unroll
    def "get xml incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming")
    }


    @Unroll
    def "get xml outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/relationship")
    }

    @Unroll
    def "get xml incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/relationship")
    }


    @Unroll
    def "get xml outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/xyz")
    }

    @Unroll
    def "get xml incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/xyz")
    }

    // -- end copy and pasted
}


