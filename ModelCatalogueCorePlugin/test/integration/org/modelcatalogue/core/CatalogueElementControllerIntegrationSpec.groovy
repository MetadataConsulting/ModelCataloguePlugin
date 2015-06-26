package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus

/**
 * Created by adammilward on 27/02/2014.
 */
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
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
       [name: "Something", description: "blah blah blah"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
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
