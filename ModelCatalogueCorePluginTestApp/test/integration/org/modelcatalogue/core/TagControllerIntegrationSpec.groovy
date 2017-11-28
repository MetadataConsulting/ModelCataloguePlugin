package org.modelcatalogue.core

import grails.util.GrailsNameUtils

class TagControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit() {
        [name: "Tag #13", description: "edited description ", symbol: "R", dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
       [symbol: "Tag #14", name: "Something", description: "blah blah blah", dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        Tag
    }

    @Override
    AbstractCatalogueElementController getController() {
        new TagController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Tag getLoadItem() {
        Tag.findByName("Tag #1")
    }

    @Override
    Tag getAnotherLoadItem() {
        Tag.findByName("Tag #2")
    }

}
