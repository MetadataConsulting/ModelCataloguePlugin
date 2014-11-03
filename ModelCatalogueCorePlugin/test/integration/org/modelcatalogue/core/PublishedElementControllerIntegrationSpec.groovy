package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
 * Created by adammilward on 27/02/2014.
 */
class PublishedElementControllerIntegrationSpec extends AbstractPublishedElementControllerIntegrationSpec {

    def setupSpec(){
        totalCount = PublishedElement.count()
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
        PublishedElement
    }

    @Override
    AbstractCatalogueElementController getController() {
        new PublishedElementController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Model getLoadItem() {
        Model.findByName("mTest5")
    }

    @Override
    Model getAnotherLoadItem() {
        Model.findByName("mTest6")
    }

    protected CatalogueElement newResourceInstance() {
        return new Model()
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
    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, PublishedElement.countByStatus(ElementStatus.FINALIZED), "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, PublishedElement.countByStatus(ElementStatus.FINALIZED), "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, PublishedElement.countByStatus(ElementStatus.FINALIZED), "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, PublishedElement.countByStatus(ElementStatus.FINALIZED), "${baseLink}?max=4&offset=12", "${baseLink}?max=4&offset=4"],
                [5, 10, 10, 10, PublishedElement.countByStatus(ElementStatus.FINALIZED), "/publishedElement/?max=10&offset=20", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, PublishedElement.countByStatus(ElementStatus.FINALIZED), "${baseLink}?max=2&offset=12", "${baseLink}?max=2&offset=8"]
        ]
    }

    @Override
    protected getTotalRowsExported() {
        PublishedElement.countByStatus(ElementStatus.FINALIZED)
    }
}
