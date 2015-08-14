package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

class PrimitiveTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ",  dataModels: dataModelsForSpec]
    }

    @Override
    Map getNewInstance(){
        [name: "ptTest2123", dataModels: dataModelsForSpec, measurementUnit: CatalogueElementMarshaller.minimalCatalogueElementJSON(notNull(MeasurementUnit.findByName("Degrees Celsius")))]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf", dataModels: dataModelsForSpec]
    }

    @Override
    Class getResource() {
        PrimitiveType
    }

    @Override
    AbstractCatalogueElementController getController() {
        new PrimitiveTypeController()
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
    PrimitiveType getLoadItem() {
        PrimitiveType.findByName("Primitive Test 1")
    }

    @Override
    PrimitiveType getAnotherLoadItem() {
        PrimitiveType.findByName("Primitive Test 2")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        assert json.measurementUnit
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        assert json.measurementUnit
        return true

    }
}
