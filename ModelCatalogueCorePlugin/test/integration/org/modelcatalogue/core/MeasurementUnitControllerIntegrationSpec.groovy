package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
 * Created by adammilward on 27/02/2014.
 */
class MeasurementUnitControllerIntegrationSpec extends AbstractPublishedElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", symbol: "R"]
    }

    @Override
    Map getNewInstance(){
       [symbol: "FS", name: "Something", description: "blah blah blah"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
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
