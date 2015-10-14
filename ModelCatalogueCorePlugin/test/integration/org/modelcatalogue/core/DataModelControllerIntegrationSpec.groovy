package org.modelcatalogue.core

import grails.util.GrailsNameUtils

class DataModelControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
       [name:"new classification", description: "the classification of the university2"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    Class getResource() {
        DataModel
    }

    @Override
    AbstractCatalogueElementController getController() {
        new DataModelController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    DataModel getLoadItem() {
        DataModel.findByName("data set 1")
    }

    @Override
    DataModel getAnotherLoadItem() {
        DataModel.findByName("data set 2")
    }

    def getPaginationClassifiesParameters(baseLink){
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 12, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 12, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 12, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, 12, "", "${baseLink}?max=4&offset=4"],
                [5, 2, 10, 10, 12, "", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, 12, "", "${baseLink}?max=2&offset=8"]
        ]
    }

}
