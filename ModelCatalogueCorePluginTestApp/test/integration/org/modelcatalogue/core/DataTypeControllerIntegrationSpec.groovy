package org.modelcatalogue.core

import grails.util.GrailsNameUtils

class DataTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    protected boolean getRecord() {
        true
    }

    def setupSpec() {
        totalCount = 48
    }

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description ", dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
        [name: "test data type", dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        DataType
    }

    @Override
    AbstractCatalogueElementController getController() {
        new DataTypeController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    DataType getLoadItem() {
        DataType.findByName("boolean")
    }

    @Override
    DataType getAnotherLoadItem() {
        DataType.findByName("double")
    }

    @Override
    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 1, 1, 0, totalCount, "${baseLink}?max=1&offset=1", ""],
                [2, 1, 1, 1, totalCount, "${baseLink}?max=1&offset=2", "${baseLink}?max=1&offset=0"],
                [3, 1, 1, 2, totalCount, "${baseLink}?max=1&offset=3", "${baseLink}?max=1&offset=1"],
        ]
    }
}
