package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 27/02/2014.
 */
class ValueDomainControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    protected boolean getRecord() {
        true
    }

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
        controller.params.value         = '37'
        controller.response.format      = "json"

        controller.convert()

        def json = controller.response.json

        then:
        json.result == 98.6

    }


    @Unroll
    def "validate=#valid value #value with rule #domain.rule of domain #domain"() {
        expect:
        domain

        when:
        controller.request.method       = 'GET'
        controller.params.id            = domain.id
        controller.params.value         = value
        controller.response.format      = "json"

        controller.validateValue()

        def json = controller.response.json

        then:
        json.result == valid

        where:
        valid | value | domain
        true  | '10'  | ValueDomain.findByName("value domain Celsius")
        true  | '10'  | ValueDomain.findByName("value domain Fahrenheit")

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
        ValueDomain domain = ValueDomain.findByName("school subject")

        domain.addToConceptualDomains(conceptualDomain)
        conceptualDomain.addToValueDomains(domain)

        domain
    }

    @Override
    ValueDomain getAnotherLoadItem() {
        ValueDomain domain = ValueDomain.findByName("school subject2")

        domain.addToConceptualDomains(conceptualDomain)
        conceptualDomain.addToValueDomains(domain)

        domain
    }

    ConceptualDomain getConceptualDomain() {
        ConceptualDomain.findByName('cdtest1')
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
        ValueDomain domain = instance as ValueDomain

        for (ConceptualDomain conceptualDomain in domain.conceptualDomains) {
            conceptualDomain.removeFromValueDomains(domain)
            domain.removeFromConceptualDomains(conceptualDomain)
        }
        true
    }

}
