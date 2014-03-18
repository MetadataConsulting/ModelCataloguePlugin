package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.util.marshalling.*
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ValueDomainController)
@Mock([DataElement, ValueDomain, Relationship, RelationshipType, MeasurementUnit, DataType, Mapping])
class ValueDomainControllerSpec extends CatalogueElementRestfulControllerSpec {

    def author, mph, integer, kph
    RelationshipType type

    def setup() {
        controller.relationshipService = new RelationshipService()
        controller.mappingService = new MappingService()
        fixturesLoader.load('measurementUnits/MU_kph', 'dataElements/DE_author', 'measurementUnits/MU_milesPerHour', 'dataTypes/DT_integer', 'relationshipTypes/RT_relationship')

        new ValueDomainMarshaller().register()
        assert (type = fixturesLoader.RT_relationship.save())
        assert (mph = fixturesLoader.MU_milesPerHour.save())
        assert (kph = fixturesLoader.MU_kph.save())
        assert (integer = fixturesLoader.DT_integer.save())
        assert (author = fixturesLoader.DE_author.save())
        assert (loadItem1 = new ValueDomain(name: "ground_speed", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert (loadItem2 = new ValueDomain(name: "ground_speed5", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer).save())
        assert !(new RelationshipService().link(loadItem1, author, type).hasErrors())

        //configuration properties for abstract controller
        assert (newInstance = new ValueDomain(name: "ground_speed2", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer))
        assert (badInstance = new ValueDomain(name: "", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer))
        assert (propertiesToEdit = [description: "something different"])
        //assert (propertiesToCheck = ['name', 'description', 'unitOfMeasure.name', 'dataType.@id'])

    }

    def cleanup() {
        author?.delete()
        mph?.delete()
        integer?.delete()
        type?.delete()
    }


    def xmlCustomPropertyCheck(xml, item){

        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.unitOfMeasure.name, item.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(xml.dataType.@id, item.dataType.id, "dataType")
        return true
    }

    def xmlCustomPropertyCheck(inputItem, xml, outputItem){

        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.unitOfMeasure.name, inputItem.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(xml.dataType.@id, inputItem.dataType.id, "dataType")

        return true
    }


    def customJsonPropertyCheck(item, json){

        super.customJsonPropertyCheck(item, json)
        checkProperty(json.unitOfMeasure.name, item.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(json.dataType.id, item.dataType.id, "dataType")

        return true
    }


    def customJsonPropertyCheck(inputItem, json, outputItem){

        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.unitOfMeasure.name, inputItem.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(json.dataType.id, inputItem.dataType.id, "dataType")

        return true

    }



    Class getResource() { ValueDomain }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        if (!MeasurementUnit.findByName("MPH")) assert (mph = new MeasurementUnit(name: "MPH").save()) else mph = MeasurementUnit.findByName("MPH")
        if (!DataType.findByName("integer")) assert (integer = new DataType(name: "integer").save()) else integer = DataType.findByName("integer")
        [name: "ground_speed_${counter}", unitOfMeasure: mph, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: integer]
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new ValueDomainMarshaller(), new DataElementMarshaller(), new MappingsMarshaller(), new MappingMarshallers(), new MeasurementUnitMarshallers()]
    }

    @Unroll
    def "get json mapping: #no where max: #max offset: #offset\""() {
        fillWithDummyEntities(15)
        ValueDomain first = ValueDomain.get(1)
        mapToDummyEntities(first)

        when:
        params.id = first.id
        params.offset = offset
        params.max = max
        response.format = "json"
        controller.mappings(max)
        def json = response.json

        recordResult "mapping$no", json


        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)

        when:
        def item  = json.list[0]
        def mapping = first.outgoingMappings.find {it.id == item.id}

        then:
        item.mapping == mapping.mapping
        item.destination
        item.destination.id == mapping.destination.id
        item.destination.elementType
        item.destination.elementType == mapping.destination.class.name

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/mapping")
    }


    @Unroll
    def "get xml mapping: #no where max: #max offset: #offset\""() {
        fillWithDummyEntities(15)
        ValueDomain first = ValueDomain.get(1)
        mapToDummyEntities(first)

        when:
        params.id = first.id
        params.offset = offset
        params.max = max
        response.format = "xml"
        controller.mappings(max)
        def xml = response.xml

        recordResult "mapping$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.mapping.size() == size


        when:
        def item  = xml.mapping[0]
        def mapping = first.outgoingMappings.find {it.id == item.@id.text() as Long}

        then:
        item.mapping.text() == mapping.mapping
        item.destination
        item.destination.@id.text() == "$mapping.destination.id"
        item.destination.@elementType
        item.destination.@elementType.text() == mapping.destination.class.name

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/mapping")
    }


    @Unroll
    def "return 404 for non existing domain calling #method method with #format format"() {
        response.format = format
        params.id = 1000000

        when:
        controller."$method"()

        then:
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format | method
        "json" | "mappings"
        "xml"  | "mappings"
        "json" | "addMapping"
        "xml"  | "addMapping"
        "json" | "removeMapping"
        "xml"  | "removeMapping"
    }

    @Unroll
    def "return 404 for non existing other side calling #method method with #format format"() {
        response.format = format
        params.id = loadItem1.id
        params.destination = 10000000

        request."$format" = payload

        when:
        controller."$method"()

        then:
        response.status == status

        where:
        format | method          | payload
        "json" | "addMapping"    | """{"mapping":"x"}"""
        "json" | "removeMapping" | """{"mapping":"x"}"""
        "xml"  | "addMapping"    | """<mapping>x</mapping>"""
        "xml"  | "removeMapping" | """<mapping>x</mapping>"""
    }

    @Unroll
    def "Map existing domains with failing constraint #format"(){
        response.format = format
        request."$format" = payload

        params.id           = loadItem1.id
        params.destination  = loadItem2.id

        controller.addMapping()
        def result = response."$format"

        recordResult "addMappingFailed", result

        expect:
        response.status == 422 // unprocessable entity
        test.call(result)

        where:
        format | payload                      | test
        "json" | """{"mapping":"y"}"""        | { it.errors && it.errors.first().field == "mapping" }
        "xml"  | """<mapping>y</mapping>"""   | { it.name() == "errors" && it.error[0].@field.text() == "mapping" }
    }

    @Unroll
    def "unmap non existing mapping will return 404 for #format request"(){
        response.format = format

        controller.mappingService.unmap(loadItem1, loadItem2)

        params.id           = loadItem1.id
        params.destination  = loadItem2.id

        controller.removeMapping()

        expect:
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format << ["json", "xml"]
    }


    @Unroll
    def "unmap existing mapping will return 204 for #format request"(){
        response.format = format

        controller.mappingService.map(loadItem1, loadItem2, [one: "one"])

        params.id           = loadItem1.id
        params.destination  = loadItem2.id

        controller.removeMapping()

        expect:
        response.status == HttpServletResponse.SC_NO_CONTENT

        where:
        format << ["json", "xml"]
    }

    def "map valid domains with json"() {
        response.format = "json"
        request.json = """{"mapping":"x"}"""

        params.id           = loadItem1.id
        params.destination  = loadItem2.id

        controller.addMapping()

        def json = response.json

        recordResult "addMapping", json

        expect:
        json.mapping            == "x"
        json.source
        json.source.id          == loadItem1.id
        json.source.link        == loadItem1.info.link
        json.destination
        json.destination.id     == loadItem2.id
        json.destination.link   == loadItem2.info.link
    }


    def "map valid domains with xml"() {
        response.format = "xml"
        request.xml = """<mapping>x</mapping>"""

        params.id           = loadItem1.id
        params.destination  = loadItem2.id

        controller.addMapping()

        def xml = response.xml

        recordResult "addMapping", xml

        expect:
        xml.mapping.text()            == "x"
        xml.source
        xml.source.@id.text()         == "$loadItem1.id"
        xml.source.link.text()        == loadItem1.info.link
        xml.source.name.text()        == loadItem1.name
        xml.destination
        xml.destination.@id.text()    == "$loadItem2.id"
        xml.destination.link.text()   == loadItem2.info.link
        xml.destination.name.text()   == loadItem2.name
    }

    protected mapToDummyEntities(ValueDomain toBeLinked) {
        for (domain in resource.list()) {
            if (domain != toBeLinked) {
                controller.mappingService.map(toBeLinked, domain, "x")
                if (toBeLinked.outgoingMappings.size() == 12) {
                    break
                }
            }
        }

        assert toBeLinked.outgoingMappings
        assert toBeLinked.outgoingMappings.size() == 12
        toBeLinked
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


