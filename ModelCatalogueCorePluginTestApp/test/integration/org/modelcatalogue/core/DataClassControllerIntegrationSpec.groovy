package org.modelcatalogue.core

import grails.util.GrailsNameUtils

class DataClassControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    protected boolean getRecord() {
        true
    }

    def setupSpec() {
        totalCount = 12
    }

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description ", dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
       [name:"new model", description: "the model of the book", dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
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


    def getPaginationParameters(String baseLink) {
        [
                [1, 10, 10, 0, 12, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 12, "${baseLink}?max=5&offset=5", ""],
                [3, 2, 5, 10,12, "", "${baseLink}?max=5&offset=5"],
        ]
    }

}
