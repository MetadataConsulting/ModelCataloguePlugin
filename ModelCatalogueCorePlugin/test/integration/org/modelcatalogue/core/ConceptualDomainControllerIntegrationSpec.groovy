package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
 * Created by adammilward on 27/02/2014.
 */
class ConceptualDomainControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
       [name:"new ConceptualDomain", description: "the ConceptualDomain of the university2"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    Class getResource() {
        ConceptualDomain
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ConceptualDomainController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    ConceptualDomain getLoadItem() {
        ConceptualDomain.findByName("university libraries")
    }

    @Override
    ConceptualDomain getAnotherLoadItem() {
        ConceptualDomain.findByName("school libraries")
    }


}
