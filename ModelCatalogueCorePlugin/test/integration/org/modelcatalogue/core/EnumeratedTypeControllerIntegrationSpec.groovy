package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import spock.lang.Unroll


class EnumeratedTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {


    @Unroll
    def "get json valueDomains: #no where max: #max offset: #offset"() {
        EnumeratedType first = loadItem
        createValueDomainsUsingEnumeratedType(first, 12)

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
        resource.count() == 12

        cleanup:
        deleteValueDomains(first, 12)

        where:
        [no, size, max, offset, total, next, previous] << optimize(getPaginationParameters("/${resourceName}/${loadItem.id}/valueDomain"))
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", enumerations:['d26':'test28', 'sadf':'asdgsadg'], dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
        [name: "etTest2123", enumerations:['d2n':'test2123', 't':'asdfsadfsadf'], dataModels: dataModelsForSpec]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
    }

    @Override
    Class getResource() {
        EnumeratedType
    }

    @Override
    AbstractCatalogueElementController getController() {
        new EnumeratedTypeController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    EnumeratedType getLoadItem() {
        EnumeratedType.findByName("gender")
    }

    @Override
    EnumeratedType getAnotherLoadItem() {
        EnumeratedType.findByName("sub1")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        assert json.enumerations
        assert json.enumerations.type == 'orderedMap'
        assert json.enumerations.values
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        assert json.enumerations
        assert json.enumerations.type == 'orderedMap'
        assert json.enumerations.values
        return true

    }


    private createValueDomainsUsingEnumeratedType(EnumeratedType enumeratedType, Integer max){
        max.times {new ValueDomain(name: "dataTypeValueDomain${it}", description: "the ground speed of the moving vehicle", dataType: enumeratedType).save()}
    }

    private deleteValueDomains(EnumeratedType enumeratedType, Integer max){
        max.times {
            enumeratedType.removeFromRelatedValueDomains(ValueDomain.findByName("dataTypeValueDomain${it}"))
        }
    }




}
