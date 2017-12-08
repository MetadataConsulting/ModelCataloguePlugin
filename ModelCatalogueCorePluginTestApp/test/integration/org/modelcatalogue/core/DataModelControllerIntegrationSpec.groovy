package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import spock.lang.Ignore

@Ignore
class DataModelControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance() {
       [name:"new classification", description: "the classification of the university2"]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf"]
    }

    protected  getBadNameJSON() {
        [name: "g" * 300]
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
        DataModel.findByName("data set 2")
    }

    @Override
    DataModel getAnotherLoadItem() {
        DataModel.findByName("data set 3")
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

    def getHistoryPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 1, 1, 0, 3, "${baseLink}?max=1&sort=semanticVersion&order=asc&offset=1", ""],
                [2, 1, 1, 1, 3, "${baseLink}?max=1&sort=semanticVersion&order=asc&offset=2", "${baseLink}?max=1&sort=semanticVersion&order=asc&offset=0"],
                [3, 1, 1, 2, 3, "", "${baseLink}?max=1&sort=semanticVersion&order=asc&offset=1"],
        ]
    }

}
