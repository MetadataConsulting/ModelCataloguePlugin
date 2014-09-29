package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.ValueDomains
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
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
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/${loadItem.id}/valueDomain")
    }


    @Unroll
    def "get xml mapping: #no where max: #max offset: #offset"() {
        EnumeratedType first = loadItem
        createValueDomainsUsingEnumeratedType(first, 12)

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
        resource.count() == 12

        cleanup:
        deleteValueDomains(first, 12)

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/${loadItem.id}/valueDomain")
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", enumerations:['d26':'test28', 'sadf':'asdgsadg']]
    }

    @Override
    Map getNewInstance(){
        [name: "etTest2123", enumerations:['d2n':'test2123', 't':'asdfsadfsadf']]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.EnumeratedType] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"

        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
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
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        def xmlProp = xml.depthFirst().find { it.name() == "enumerations" }
        if (xmlProp) {
            def propMap = [:]
            xmlProp.enumeration.each{ propMap.put(it.@key.toString(), it.text()) }
            checkPropertyMapMapString(propMap, item.getProperty("enumerations"), "enumerations")
        }
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        def xmlProp = xml.depthFirst().find { it.name() == "enumerations" }
        if (xmlProp) {
            def propMap = [:]
            xmlProp.enumeration.each{ propMap.put(it.@key.toString(), it.text()) }
            checkPropertyMapMapString(propMap, outputItem.getProperty("enumerations"), "enumerations")
        }
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkObjectMapStringProperty(json.enumerations , item.enumerations, "enumerations")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkObjectMapStringProperty(json.enumerations , inputItem.enumerations, "enumerations")
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
