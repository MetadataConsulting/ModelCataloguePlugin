package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
import uk.co.mc.core.util.marshalling.RelationshipMarshallers

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
        assert (relationshipType = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is relationship of").save())
        assert (celsius = new MeasurementUnit(symbol: "°C", name: "Degrees of Celsius", description: """Celsius, also known as centigrade,[1] is a scale and unit of measurement for temperature. It is named after the Swedish astronomer Anders Celsius (1701–1744), who developed a similar temperature scale. The degree Celsius (°C) can refer to a specific temperature on the Celsius scale as well as a unit to indicate a temperature interval, a difference between two temperatures or an uncertainty. The unit was known until 1948 as "centigrade" from the Latin centum translated as 100 and gradus translated as "steps".""").save())
        assert (fahrenheit = new MeasurementUnit(symbol: "°F", name: "Degrees of Fahrenheit", description: """Fahrenheit (symbol °F) is a temperature scale based on one proposed in 1724 by the physicist Daniel Gabriel Fahrenheit (1686–1736), after whom the scale is named.[1] On Fahrenheit's original scale the lower defining point was the lowest temperature to which he could reproducibly cool brine (defining 0 degrees), while the highest was that of the average human core body temperature (defining 100 degrees). There exist several stories on the exact original definition of his scale; however, some of the specifics have been presumed lost or exaggerated with time. The scale is now usually defined by two fixed points: the temperature at which water freezes into ice is defined as 32 degrees, and the boiling point of water is defined to be 212 degrees, a 180 degree separation, as defined at sea level and standard atmospheric pressure.""").save())
        assert !Relationship.link(celsius, fahrenheit, relationshipType).hasErrors()
        new RelationshipMarshallers().register()
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
        json.name == celsius.name
        json.description == celsius.description
        json.symbol == celsius.symbol
        json.outgoingRelationships.size() == 1
        json.outgoingRelationships[0].id == celsius.outgoingRelationships[0].id
        json.outgoingRelationships[0].source == [id: celsius.id, name: celsius.name, link: "/measurementUnit/${celsius.id}"]
        json.outgoingRelationships[0].destination == [id: fahrenheit.id, name: fahrenheit.name, link: "/measurementUnit/${fahrenheit.id}"]
        json.outgoingRelationships[0].type == [id: relationshipType.id, name: relationshipType.name, link: "/relationshipType/${relationshipType.id}"]

        !json.incomingRelationships
    }

    def "Show single existing item as XML"() {
        response.format = "xml"

        params.id = "${celsius.id}"

        controller.show()

        GPathResult xml = response.xml

        expect:
        xml
        xml.@id == celsius.id
        xml.name == celsius.name
        xml.description == celsius.description
        xml.symbol == celsius.symbol
        xml.outgoingRelationships.size() == 1
        xml.outgoingRelationships.relationship[0].@id == celsius.outgoingRelationships[0].id
        xml.outgoingRelationships.relationship[0].source.@id == celsius.id
        xml.outgoingRelationships.relationship[0].source.name == celsius.name
        xml.outgoingRelationships.relationship[0].destination.@id == fahrenheit.id
        xml.outgoingRelationships.relationship[0].destination.name == fahrenheit.name
        xml.outgoingRelationships.relationship[0].type.@id == relationshipType.id
        xml.outgoingRelationships.relationship[0].type.name == relationshipType.name
        xml.outgoingRelationships.relationship[0].source.link == "/measurementUnit/${celsius.id}"
        xml.outgoingRelationships.relationship[0].destination.link == "/measurementUnit/${fahrenheit.id}"
        xml.outgoingRelationships.relationship[0].type.link == "/relationshipType/${relationshipType.id}"
    }

}
