package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
 * Created by ladin on 16.04.14.
 */
class ExtendibleElementControllerIntegrationSpec extends AbstractExtendibleElementControllerIntegrationSpec {

    def setupSpec(){
        totalCount = 48
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", ext: [["testkey":"testValue"]]]
    }


    @Override
    Class getResource() {
        ExtendibleElement
    }

    @Override
    protected CatalogueElement newResourceInstance() {
        return new Model()
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ExtendibleElementController()
    }

    @Override
    Map getNewInstance(){
        [name: "Something", description: "blah blah blah", ext: [['testkey': 'testvalue']]]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Model getLoadItem() {
        Model.findByName("mTest3")
    }

    @Override
    Model getAnotherLoadItem() {
        Model.findByName("mTest4")
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.MeasurementUnit] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    def getRelationshipPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 11, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 11, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 11, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 3, 4, 8, 11, "", "${baseLink}?max=4&offset=4"],
                [5, 1, 10, 10, 11, "", "${baseLink}?max=10&offset=0"],
                [6, 1, 2, 10, 11, "", "${baseLink}?max=2&offset=8"]
        ]
    }

    @Override
    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 48, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 48, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 48, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, 48, "${baseLink}?max=4&offset=12", "${baseLink}?max=4&offset=4"],
                [5, 10, 10, 10, 48, "${baseLink}?max=10&offset=20", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, 48, "${baseLink}?max=2&offset=12", "${baseLink}?max=2&offset=8"]
        ]
    }
}
