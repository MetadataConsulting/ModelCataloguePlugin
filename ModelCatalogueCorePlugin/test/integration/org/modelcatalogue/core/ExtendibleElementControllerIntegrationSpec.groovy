package org.modelcatalogue.core

/**
 * Created by ladin on 16.04.14.
 */
class ExtendibleElementControllerIntegrationSpec extends PublishedElementControllerIntegrationSpec {

    def setupSpec(){
        totalCount = 24
    }

    @Override
    Class getResource() {
        ExtendibleElement
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ExtendibleElementController()
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

}
