package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus

class DataClassControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    protected boolean getRecord() {
        true
    }

    def setupSpec(){
        totalCount = 12
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
       [name:"new model", description: "the model of the book", dataModels: dataModelsForSpec]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
    }

    @Override
    Class getResource() {
        DataClass
    }

    @Override
    AbstractCatalogueElementController getController() {
        new DataClassController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    DataClass getLoadItem() {
        DataClass.findByName("mTest3")
    }

    @Override
    DataClass getAnotherLoadItem() {
        DataClass.findByName("mTest4")
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

    @Override
    protected getTotalRowsExported() { 5 }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 5, 10, 0, 5, "", ""],
                [2, 5, 5, 0, 5, "", ""],
        ]
    }

}
