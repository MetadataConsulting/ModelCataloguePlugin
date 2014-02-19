package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import uk.co.mc.core.util.marshalling.DataTypeMarshaller
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
        assert (newInstance = new EnumeratedType(name: "sub4", enumAsString: ['h':'history', 'p':'politics', 'sci':'science']))
        assert (badInstance = new EnumeratedType(name: "", description:"asdf"))
        assert (propertiesToEdit = [description: "edited description "])
        assert (propertiesToCheck = ['name'])

    }

    def cleanup() {
        type.delete()
    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "ENumeratedType${counter}", enumerations:['H':'history', 'P':'politics', 'SCI':'science', 'GEO':'geography']]
    }

    Class getResource() {
        EnumeratedType
    }

}


