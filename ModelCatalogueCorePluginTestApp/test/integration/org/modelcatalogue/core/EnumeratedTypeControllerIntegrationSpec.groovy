package org.modelcatalogue.core

import grails.util.GrailsNameUtils

class EnumeratedTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {


    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description ", enumerations:['d26':'test28', 'sadf':'asdgsadg'], dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
        [name: "etTest2123", enumerations:['d2n':'test2123', 't':'asdfsadfsadf'], dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        EnumeratedType
    }

    @Override
    AbstractCatalogueElementController getController() {
        new EnumeratedTypeController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }


    @Override
    EnumeratedType getLoadItem() {
        EnumeratedType.findByName("gender")
    }

    @Override
    EnumeratedType getAnotherLoadItem() {
        EnumeratedType.findByName("sub1")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        assert json.enumerations
        assert json.enumerations.type == 'orderedMap'
        assert json.enumerations.values
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        assert json.enumerations
        assert json.enumerations.type == 'orderedMap'
        assert json.enumerations.values
        return true

    }

}
