package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.ModelMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ModelController)
@Mock([Model,DataElement, Relationship, RelationshipType])
class ModelControllerSpec extends AbstractRestfulControllerSpec {

    RelationshipType type

    def setup() {

        fixturesLoader.load('models/M_book', 'models/M_chapter1', 'dataElements/DE_author2',  'relationshipTypes/RT_relationship')
        new ModelMarshaller().register()

        assert (loadItem1 = fixturesLoader.M_book.save())
        assert (loadItem2 = fixturesLoader.DE_author2.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !Relationship.link(loadItem1, loadItem2, type).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.M_chapter1)
        assert (badInstance = new Model(name: "", description:"asdf"))
        assert (propertiesToEdit = [description: "edited description "])
        assert (propertiesToCheck = ['name','description', '@status', '@versionNumber'])


    }

    def cleanup() {
        type.delete()
    }



    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "model${counter}", description: "the model of the book"]
    }

    Class getResource() {
        Model
    }


}


