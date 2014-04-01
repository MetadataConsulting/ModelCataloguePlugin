package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
 * Created by adammilward on 27/02/2014.
 */
class ModelControllerIntegrationSpec extends CatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
       [name:"new model", description: "the model of the book"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.Model] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        Model
    }

    @Override
    CatalogueElementController getController() {
        new ModelController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Model getLoadItem() {
        Model.findByName("book")
    }

    @Override
    Model getAnotherLoadItem() {
        Model.findByName("chapter1")
    }

    @Override
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkStatusProperty(xml.@status, item.status, "status")
        checkProperty(xml.@versionNumber, item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkStatusProperty(xml.@status, outputItem.status, "status")
        checkProperty(xml.@versionNumber, outputItem.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkStatusProperty(json.status , item.status, "status")
        checkProperty(json.versionNumber , item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkStatusProperty(json.status , outputItem.status, "status")
        checkProperty(json.versionNumber , outputItem.versionNumber, "versionNumber")
        return true
    }

}
