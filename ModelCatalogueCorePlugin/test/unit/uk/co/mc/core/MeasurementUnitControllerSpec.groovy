package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.util.slurpersupport.GPathResult
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.MeasurementUnitMarshallers

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(MeasurementUnitController)
@Mock([MeasurementUnit, Relationship, RelationshipType])
class MeasurementUnitControllerSpec extends AbstractRestfulControllerSpec {

    RelationshipType relationshipType

    def setup() {
        fixturesLoader.load('measurementUnits/MU_degree_C', 'measurementUnits/MU_milesPerHour', 'measurementUnits/MU_degree_F', 'relationshipTypes/RT_relationship')
        new MeasurementUnitMarshallers().register()

        assert (relationshipType = fixturesLoader.RT_relationship.save())
        assert (loadItem1 = fixturesLoader.MU_degree_C.save())
        assert (loadItem2 = fixturesLoader.MU_degree_F.save())
        assert !Relationship.link(loadItem1, loadItem2, relationshipType).hasErrors()

        //configuration properties for abstract controller
        assert (newInstance = fixturesLoader.MU_milesPerHour)
        assert (badInstance = new MeasurementUnit(name: "", symbol: "km"))
        assert (propertiesToEdit = [symbol: "_C_"])
        assert (propertiesToCheck = ['name', 'symbol'])
    }

    def cleanup() {
        loadItem1.delete()
        loadItem2.delete()
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

    def "Create new instance from JSON"() {
        expect:
        !MeasurementUnit.findByName("Gram")

        when:
        response.format = "json"
        request.json = [name: "Gram", symbol: "g"]

        controller.save()

        def created = response.json
        def stored = MeasurementUnit.findByName("Gram")

        recordResult 'saveOk', created

        then:
        stored
        created
        created.id == stored.id
        created.name == "Gram"
        created.symbol == "g"
    }

    def "Do not create new instance from JSON if data are wrong"() {
        expect:
        !MeasurementUnit.findByName("Gram")

        when:
        response.format = "json"
        request.json = [name: "Gram", symbol: "g" * 200]

        controller.save()

        def created = response.json
        def stored = MeasurementUnit.findByName("Gram")

        recordResult 'saveErrors', created

        then:
        !stored
        created
        created.errors
        created.errors.size() == 1
        created.errors.first().field == 'symbol'
    }

    def "edit instance from JSON"() {
        def instance = MeasurementUnit.findByName(celsius.name)

        expect:
        instance


        when:
        def newDescription = "measures temperature"
        response.format = "json"
        params.id = instance.id
        request.json = [description: newDescription]

        controller.update()

        def updated = response.json

        recordResult 'updateOk', updated

        then:
        updated
        updated.id == instance.id
        updated.name == instance.name
        updated.description == newDescription
        updated.symbol == instance.symbol

    }

    def "edit instance with bad JSON"() {
        def instance = MeasurementUnit.findByName(celsius.name)

        expect:
        instance

        when:
        response.format = "json"
        params.id = instance.id
        request.json = [name: "g" * 256]

        controller.update()

        def updated = response.json

        recordResult 'updateErrors', updated

        then:
        updated
        updated.errors
        updated.errors.size() == 1
        updated.errors.first().field == 'name'


    }


    @Unroll
    def "get outgoing relationships pagination: #no where max: #max offset: #offset"() {
        RelationshipType relationshipType = fixturesLoader.RT_relationship.save() ?: RelationshipType.findByName('relationship')
        fillWithDummyEntities(15)

        expect:
        relationshipType

        when:

        def first = resource.get(1)

        first.outgoingRelationships = first.outgoingRelationships ?: []

        for (unit in resource.list()) {
            if (unit != first) {
                assert !Relationship.link(first, unit, relationshipType).hasErrors()
                if (first.outgoingRelationships.size() == 12) {
                    break
                }
            }
        }

        then:
        first.outgoingRelationships
        first.outgoingRelationships.size() == 12

        when:
        response.format = "json"

    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Measurement Unit ${counter}", symbol: "MU${counter}"]
    }

    Class getResource() {
        MeasurementUnit
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new MeasurementUnitMarshallers()]
    }
}
