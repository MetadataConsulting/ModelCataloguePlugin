package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.json.JSONObject

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 27/02/2014.
 */
class ValueDomainControllerIntegrationSpec extends AbstractExtendibleElementControllerIntegrationSpec {

    def "can covert value"() {
        ValueDomain celsius = ValueDomain.findByName("value domain Celsius")
        ValueDomain fahrenheit = ValueDomain.findByName("value domain Fahrenheit")

        expect:
        celsius
        fahrenheit

        when:
        controller.request.method       = 'GET'
        controller.params.id            = celsius.id
        controller.params.destination   = fahrenheit.id
        controller.params.value         = '37.4'
        controller.response.format      = "json"

        controller.convert()

        def json = controller.response.json

        then:
        json.result == 99.32

    }


    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
        [name: "ground_speed2", unitOfMeasure: MeasurementUnit.findByName("Miles per hour"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: DataType.findByName("integer"), conceptualDomains: [ConceptualDomain.findByName("public libraries")]]
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
    AbstractCatalogueElementController getController() {
        new ValueDomainController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    ValueDomain getLoadItem() {
        ValueDomain.findByName("value domain test3")
    }

    @Override
    ValueDomain getAnotherLoadItem() {
        ValueDomain.findByName("value domain test4")
    }

    @Override
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.unitOfMeasure.name, item.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(xml.dataType.@id, item.dataType.id, "dataType")
        checkProperty(xml.@multiple, item.multiple, "multiple")
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.unitOfMeasure.name, inputItem.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(xml.dataType.@id, inputItem.dataType.id, "dataType")
        checkProperty(xml.@multiple, outputItem.multiple, "multiple")
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkProperty(json.unitOfMeasure.name, item.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(json.dataType.id, item.dataType.id, "dataType")
        checkProperty(json.multiple, item.multiple, "multiple")

        assert json.conceptualDomains

        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.unitOfMeasure.name, inputItem.unitOfMeasure.name, "unitOfMeasure")
        checkProperty(json.dataType.id, inputItem.dataType.id, "dataType")
        checkProperty(json.multiple, outputItem.multiple, "multiple")

        assert outputItem.conceptualDomains

        return true
    }

    boolean isCheckVersion() {
        false
    }

    boolean removeAllRelations(CatalogueElement instance) {
        ValueDomain domain = instance

        for (ConceptualDomain conceptualDomain in domain.conceptualDomains) {
            conceptualDomain.removeFromValueDomains(domain)
            domain.removeFromConceptualDomains(conceptualDomain)
        }
        true
    }

}
