package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus

class CatalogueElementControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    def setupSpec(){
        totalCount = resource.countByStatus(ElementStatus.FINALIZED)
    }

    @Override
    protected Long getResourceCount() {
        resource.countByStatus(ElementStatus.FINALIZED)
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
       [name: "Something", description: "blah blah blah", dataModels: dataModelsForSpec]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
    }


    @Override
    Class getResource() {
        CatalogueElement
    }

    @Override
    AbstractCatalogueElementController getController() {
        new CatalogueElementController()
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

    protected CatalogueElement newResourceInstance() {
        return new MeasurementUnit()
    }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, totalCount, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, totalCount, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, totalCount, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"]
        ]
    }

}
