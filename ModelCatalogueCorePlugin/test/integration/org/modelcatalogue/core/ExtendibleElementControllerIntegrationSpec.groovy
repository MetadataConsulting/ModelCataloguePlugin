package org.modelcatalogue.core

/**
 * Created by ladin on 16.04.14.
 */
class ExtendibleElementControllerIntegrationSpec extends PublishedElementControllerIntegrationSpec {


    @Override
    Class getResource() {
        ExtendibleElement
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ExtendibleElementController()
    }

}
