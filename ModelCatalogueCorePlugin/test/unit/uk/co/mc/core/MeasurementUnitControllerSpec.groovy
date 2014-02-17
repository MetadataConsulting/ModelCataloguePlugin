package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
import uk.co.mc.core.util.marshalling.MeasurementUnitMarshallers

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(MeasurementUnitController)
@Mock([MeasurementUnit, Relationship, RelationshipType])
class MeasurementUnitControllerSpec extends AbstractRestfulControllerSpec {

    MeasurementUnit celsius
    MeasurementUnit fahrenheit
    RelationshipType relationshipType

    def setup() {
        fixturesLoader.load('measurementUnits/MU_degree_C', 'measurementUnits/MU_degree_F', 'relationshipTypes/RT_relationship')
        assert (relationshipType = fixturesLoader.RT_relationship.save())
        assert (celsius = fixturesLoader.MU_degree_C.save())
        assert (fahrenheit = fixturesLoader.MU_degree_F.save())
        assert !Relationship.link(celsius, fahrenheit, relationshipType).hasErrors()
        new MeasurementUnitMarshallers().register()
    }

    def cleanup() {
        celsius.delete()
        fahrenheit.delete()
        relationshipType.delete()
    }

    def "Show single existing item as JSON"() {
        response.format = "json"

        params.id = "${celsius.id}"

        controller.show()

        def json = response.json

        recordResult 'showOne', json

        expect:
        json
        json.id == celsius.id
        json.version == celsius.version
        json.name == celsius.name
        json.description == celsius.description
        json.symbol == celsius.symbol
        json.outgoingRelationships == [count: 1, link: "/measurementUnit/outgoing/${celsius.id}"]
        json.incomingRelationships == [count: 0, link: "/measurementUnit/incoming/${celsius.id}"]
    }

    def "Show single existing item as XML"() {
        response.format = "xml"

        params.id = "${celsius.id}"

        controller.show()

        GPathResult xml = response.xml

        expect:
        xml
        xml.@id == celsius.id
        xml.@version == celsius.version
        xml.name == celsius.name
        xml.description == celsius.description
        xml.symbol == celsius.symbol
        xml.outgoingRelationships.@count == 1
        xml.outgoingRelationships.@link == "/measurementUnit/outgoing/${celsius.id}"
        xml.incomingRelationships.@count == 0
        xml.incomingRelationships.@link == "/measurementUnit/incoming/${celsius.id}"

    }

}
