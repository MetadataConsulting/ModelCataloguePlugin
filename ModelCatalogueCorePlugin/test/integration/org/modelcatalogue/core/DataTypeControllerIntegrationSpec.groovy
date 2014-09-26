package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class DataTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    def setupSpec(){
        totalCount = 24
    }


    @Unroll
    def "get json valueDomains: #no where max: #max offset: #offset"() {
        DataType first = loadItem
        createValueDomainsUsingDataType(first, 12)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "json"
        controller.valueDomains(max)
        def json = controller.response.json
        recordResult "valueDomains$no", json

        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)

        when:
        def item  = json.list[0]
        def valueDomain = first.relatedValueDomains.find {it.id == item.id}

        then:
        item.id == valueDomain.id
        item.dataType.id == valueDomain.dataType.id
        resource.count() == totalCount

        cleanup:
        deleteValueDomains(first, 12)

        where:
        [no, size, max, offset, total, next, previous] << getPaginationValueDomainsParameters("/${resourceName}/${loadItem.id}/valueDomain")
    }


    @Unroll
    def "get value domains: #no where max: #max offset: #offset"() {
        DataType first = loadItem
        createValueDomainsUsingDataType(first, 12)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "xml"
        controller.valueDomains(max)
        def xml = controller.response.xml
        recordResult "valueDomains$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.valueDomain.size() == size

        when:
        def item  = xml.valueDomain[0]
        def valueDomain = first.relatedValueDomains.find {it.id == item.@id.text() as Long}

        then:
        item.@id == valueDomain.id
        item.dataType.@id == valueDomain.dataType.id
        resource.count() == totalCount

        cleanup:
        deleteValueDomains(first, 12)


        where:
        [no, size, max, offset, total, next, previous] << getPaginationValueDomainsParameters("/${resourceName}/${loadItem.id}/valueDomain")
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
        [name: "test data type"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.DataType] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"

        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        DataType
    }

    @Override
    AbstractCatalogueElementController getController() {
        new DataTypeController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    DataType getLoadItem() {
        DataType.findByName("boolean")
    }

    @Override
    DataType getAnotherLoadItem() {
        DataType.findByName("double")
    }

    @Override
    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 24, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 24, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 24, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 20, 24, "", "${baseLink}?max=4&offset=16"],
                [5, 2, 10, 22, 24, "", "${baseLink}?max=10&offset=12"],
                [6, 2, 2, 22, 24, "", "${baseLink}?max=2&offset=20"]
        ]
    }

    def getPaginationValueDomainsParameters(baseLink){
            [
                    // no,size, max , off. tot. next                           , previous
                    [1, 10, 10, 0, 12, "${baseLink}?max=10&offset=10", ""],
                    [2, 5, 5, 0, 12, "${baseLink}?max=5&offset=5", ""],
                    [3, 5, 5, 5, 12, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                    [4, 4, 4, 8, 12, "", "${baseLink}?max=4&offset=4"],
                    [5, 2, 10, 10, 12, "", "${baseLink}?max=10&offset=0"],
                    [6, 2, 2, 10, 12, "", "${baseLink}?max=2&offset=8"]
            ]
    }

//    def getMappingPaginationParameters(baseLink){
//        [
//                // no,size, max , off. tot. next                           , previous
//                [1, 2, 10, 0, 2, "", ""],
//                [2, 2, 5, 0, 2, "", ""],
//        ]
//
//    }


    private createValueDomainsUsingDataType(DataType DataType, Integer max){
        max.times {new ValueDomain(name: "dataTypeValueDomain${it}", description: "the ground speed of the moving vehicle", dataType: DataType).save()}
    }

    private deleteValueDomains(DataType DataType, Integer max){
        max.times {
            DataType.removeFromRelatedValueDomains(ValueDomain.findByName("dataTypeValueDomain${it}"))
        }
    }





}
