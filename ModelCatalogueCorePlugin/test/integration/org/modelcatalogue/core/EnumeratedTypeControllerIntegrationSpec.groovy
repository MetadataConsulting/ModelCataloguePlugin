package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import spock.lang.Unroll


class EnumeratedTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {


    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", enumerations:['d26':'test28', 'sadf':'asdgsadg'], dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
        [name: "etTest2123", enumerations:['d2n':'test2123', 't':'asdfsadfsadf'], dataModels: dataModelsForSpec]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
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
    protected String getResourceNameForHistory() {
        'dataType'
    }


    protected String getItemTypeForHistory() {
        DataType.name
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
