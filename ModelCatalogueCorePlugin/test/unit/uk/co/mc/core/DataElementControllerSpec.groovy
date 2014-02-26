package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll
import uk.co.mc.core.util.ResultRecorder
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataElementController)
@Mixin(ResultRecorder)
@Mock([DataElement, Relationship, RelationshipType, ExtensionValue])
class DataElementControllerSpec extends CatalogueElementRestfulControllerSpec {

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
        assert (propertiesToCheck = ['name', 'description', 'code', '@status', '@versionNumber', 'ext'])


    }

    def cleanup() {
        type.delete()
    }

//overrider xml property check so we can look at extensions

    @Override
    boolean xmlPropertyCheck(xml, loadItem){

        def xmlProp = (xml["name"].toString())?:null

        if (xmlProp != loadItem["name"]) {
            throw new AssertionError("error: property to check: name  where xml:${xml["name"] } !=  item:${loadItem.getProperty("name")}")
        }

        xmlProp = (xml["description"].toString())?:null

        if (xmlProp != loadItem.getProperty("description")) {
            throw new AssertionError("error: property to check: description  where xml:${xml["description"] } !=  item:${loadItem.getProperty("description")}")
        }

        xmlProp = (xml["code"].toString())?:null

        if (xmlProp != loadItem.getProperty("code")) {
            throw new AssertionError("error: property to check: description  where xml:${xml["code"] } !=  item:${loadItem.getProperty("code")}")
        }

        xmlProp = (xml["@status"].toString())?:null

        if (xmlProp != loadItem.getProperty("status").toString()) {
            throw new AssertionError("error: property to check: description  where xml:${xml["@status"] } !=  item:${loadItem.getProperty("status")}")
        }

        xmlProp = (xml["@versionNumber"].toString())?:null

        if (xmlProp != loadItem.getProperty("versionNumber").toString()) {
            throw new AssertionError("error: property to check: description  where xml:${xml["@versionNumber"] } !=  item:${loadItem.getProperty("versionNumber")}")
        }



        loadItem = loadItem.getProperty("ext")

        loadItem.each{ key, value ->
            def extension = xml.depthFirst().find{it.name()=="extension" && it.@key == key}
            if(value!=extension.toString()){
                throw new AssertionError("error: property to check: extension  where xml:${xmlProp} !=  item:${loadItem.getProperty("enumerations")}")

            }
        }


        return true

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


