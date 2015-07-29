package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus


class MeasurementUnitControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {


    def "when trying to create unit with the same name the existing is returned"() {
        if (controller.readOnly) return

        MeasurementUnit expected = loadItem

        expect:
        expected

        when:
        controller.response.format = "json"
        controller.request.json = [name: "Degrees Celsius"]
        controller.save()
        def created = controller.response.json

        then:
        created
        created.latestVersionId == (expected.latestVersionId ?: expected.id)
    }

    def "when trying to create unit with the same name error is shown if the params contains more than name"() {
        if (controller.readOnly) return

        MeasurementUnit expected = loadItem

        expect:
        expected

        when:
        controller.response.format = "json"
        controller.request.json = [name: "Degrees Celsius", description: "Some Desc"]
        controller.save()
        def created = controller.response.json

        then:
        created
        created.errors
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", symbol: "R", dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
       [symbol: "FS", name: "Something", description: "blah blah blah", dataModels: dataModelsForSpec]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
    }


    @Override
    Class getResource() {
        MeasurementUnit
    }

    @Override
    AbstractCatalogueElementController getController() {
        new MeasurementUnitController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    MeasurementUnit getLoadItem() {
        MeasurementUnit.findByName("Degrees Celsius")
    }

    @Override
    MeasurementUnit getAnotherLoadItem() {
        MeasurementUnit.findByName("Kilometers per hour")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkStringProperty(json.symbol , item.symbol, "symbol")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.symbol , inputItem.symbol, "symbol")
        return true
    }

}
