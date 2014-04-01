package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.Mappings
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 27/02/2014.
 */
class ValueDomainControllerIntegrationSpec extends CatalogueElementControllerIntegrationSpec {


    @Unroll
    def "get json mapping: #no where max: #max offset: #offset\""() {
        ValueDomain first = loadItem
        mapToDummyEntities(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "json"
        controller.mappings(max)
        def json = controller.response.json

        recordResult "mapping$no", json


        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)
        json.listType == Mappings.name
        json.itemType == Mapping.name

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
        [no, size, max, offset, total, next, previous] << getMappingPaginationParameters("/${resourceName}/${loadItem.id}/mapping")
    }


    @Unroll
    def "get xml mapping: #no where max: #max offset: #offset"() {
        ValueDomain first = loadItem
        mapToDummyEntities(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "xml"
        controller.mappings(max)
        def xml = controller.response.xml

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
        [no, size, max, offset, total, next, previous] << getMappingPaginationParameters("/${resourceName}/${loadItem.id}/mapping")
    }


    @Unroll
    def "return 404 for non existing domain calling #method method with #format format"() {
        controller.response.format = format
        controller.params.id = 1000000

        when:
        controller."$method"()

        then:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

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
        controller.response.format = format
        controller.params.id = loadItem.id
        controller.params.destination = 10000000

        controller.request."$format" = payload

        when:
        controller."$method"()

        then:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format | method          | payload
        "json" | "addMapping"    | """{"mapping":"x"}"""
        "json" | "removeMapping" | """{"mapping":"x"}"""
        "xml"  | "addMapping"    | """<mapping>x</mapping>"""
        "xml"  | "removeMapping" | """<mapping>x</mapping>"""
    }

    @Unroll
    def "Map existing domains with failing constraint #format"(){
        controller.response.format = format
        controller.request."$format" = payload
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def result = controller.response."$format"
        recordResult "addMappingFailed", result

        expect:
        controller.response.status == 422 // unprocessable entity
        test.call(result)

        where:
        format | payload                      | test
        "json" | """{"mapping":"y"}"""        | { it.errors && it.errors.first().field == "mapping" }
        "xml"  | """<mapping>y</mapping>"""   | { it.name() == "errors" && it.error[0].@field.text() == "mapping" }
    }

    @Unroll
    def "unmap non existing mapping will return 404 for #format request"(){
        controller.response.format = format
        controller.mappingService.unmap(loadItem, anotherLoadItem)
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.removeMapping()

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format << ["json", "xml"]
    }


    @Unroll
    def "unmap existing mapping will return 204 for #format request"(){
        controller.response.format = format

        controller.mappingService.map(loadItem, anotherLoadItem, [one: "one"])
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.removeMapping()

        expect:
        controller.response.status == HttpServletResponse.SC_NO_CONTENT

        where:
        format << ["json", "xml"]
    }

    def "map valid domains with json"() {
        controller.response.format = "json"
        controller.request.json = """{"mapping":"x"}"""
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def json = controller.response.json
        recordResult "addMapping", json

        expect:
        json.mapping            == "x"
        json.source
        json.source.id          == loadItem.id
        json.source.link        == loadItem.info.link
        json.destination
        json.destination.id     == anotherLoadItem.id
        json.destination.link   == anotherLoadItem.info.link
    }


    def "map valid domains with xml"() {
        controller.response.format = "xml"
        controller.request.xml = """<mapping>x</mapping>"""
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def xml = controller.response.xml
        recordResult "addMapping", xml

        expect:
        xml.mapping.text()            == "x"
        xml.source
        xml.source.@id.text()         == "$loadItem.id"
        xml.source.link.text()        == loadItem.info.link
        xml.source.name.text()        == loadItem.name
        xml.destination
        xml.destination.@id.text()    == "$anotherLoadItem.id"
        xml.destination.link.text()   == anotherLoadItem.info.link
        xml.destination.name.text()   == anotherLoadItem.name
    }

    protected mapToDummyEntities(ValueDomain toBeLinked) {
        for (domain in resource.list()) {
            if (domain != toBeLinked) {
                controller.mappingService.map(toBeLinked, domain, "x")
                if (toBeLinked.outgoingMappings.size() == 11) {
                    break
                }
            }
        }

        assert toBeLinked.outgoingMappings
        assert toBeLinked.outgoingMappings.size() == 11
        toBeLinked
    }



    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
        [name: "ground_speed2", unitOfMeasure: MeasurementUnit.findByName("Miles per hour"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: DataType.findByName("integer")]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.ValueDomain] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        ValueDomain
    }

    @Override
    CatalogueElementController getController() {
        new ValueDomainController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    ValueDomain getLoadItem() {
        ValueDomain.findByName("value domain Celsius")
    }

    @Override
    ValueDomain getAnotherLoadItem() {
        ValueDomain.findByName("value domain Fahrenheit")
    }

    @Override
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.unitOfMeasure.name, item.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(xml.dataType.@id, item.dataType.id, "dataType")
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.unitOfMeasure.name, inputItem.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(xml.dataType.@id, inputItem.dataType.id, "dataType")
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkProperty(json.unitOfMeasure.name, item.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(json.dataType.id, item.dataType.id, "dataType")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.unitOfMeasure.name, inputItem.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(json.dataType.id, inputItem.dataType.id, "dataType")
        return true
    }

    def getMappingPaginationParameters(baseLink){
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 11, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 11, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 11, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 3, 4, 8, 11, "", "${baseLink}?max=4&offset=4"],
                [5, 1, 10, 10, 11, "", "${baseLink}?max=10&offset=0"],
                [6, 1, 2, 10, 11, "", "${baseLink}?max=2&offset=8"]
        ]

    }



}
