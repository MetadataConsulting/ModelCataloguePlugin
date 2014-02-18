package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import uk.co.mc.core.util.marshalling.DataTypeMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataTypeController)
@Mock([DataType, Relationship, RelationshipType, Model])
class DataTypeControllerSpec extends AbstractRestfulControllerSpec {

    RelationshipType type

    def setup() {
        new DataTypeMarshaller().register()
        fixturesLoader.load('dataTypes/DT_integer', 'dataTypes/DT_double', 'dataTypes/DT_string', 'relationshipTypes/RT_relationship')

        assert (loadItem1 = fixturesLoader.DT_integer.save())
        assert (loadItem2 = fixturesLoader.DT_double.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(loadItem1, loadItem2, type).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.DT_string)
        assert (badInstance = new DataType(name: "", description:"asdf"))
        assert (propertiesToEdit = [description: "edited description "])
        assert (propertiesToCheck = ['name','description'])

    }

    def cleanup() {
        type.delete()
    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Concept${counter}", description: "the conceptual domain"]
    }

    Class getResource() {
        DataType
    }

}


