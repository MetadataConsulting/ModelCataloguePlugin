package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import spock.lang.Unroll

class DataTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    protected boolean getRecord() {
        true
    }

    def setupSpec(){
        totalCount = 36
    }


    @Unroll
    def "get json valueDomains: #no where max: #max offset: #offset"() {
        DataType first = loadItem
        createValueDomainsUsingDataType(first, 3)

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
        deleteValueDomains(first, 3)

        where:
        [no, size, max, offset, total, next, previous] << getMappingPaginationParameters("/${resourceName}/${loadItem.id}/valueDomain")
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
        [name: "test data type", dataModels: dataModelsForSpec]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
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
                [1, 1, 1, 0, totalCount, "${baseLink}?max=1&offset=1", ""],
                [2, 1, 1, 1, totalCount, "${baseLink}?max=1&offset=2", "${baseLink}?max=1&offset=0"],
                [3, 1, 1, 2, totalCount, "${baseLink}?max=1&offset=3", "${baseLink}?max=1&offset=1"],
        ]
    }


    private static createValueDomainsUsingDataType(DataType DataType, Integer max) {
        max.times {new ValueDomain(name: "dataTypeValueDomain${it}", description: "the ground speed of the moving vehicle", dataType: DataType).save()}
    }

    private static deleteValueDomains(DataType DataType, Integer max) {
        max.times {
            DataType.removeFromRelatedValueDomains(ValueDomain.findByName("dataTypeValueDomain${it}"))
        }
    }

}
