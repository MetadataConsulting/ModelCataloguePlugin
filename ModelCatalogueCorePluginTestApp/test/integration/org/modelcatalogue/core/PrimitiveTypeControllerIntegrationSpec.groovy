package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import spock.lang.Ignore

@Ignore
class PrimitiveTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description ",  dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
        [name: "ptTest2123", dataModel: dataModelForSpec, measurementUnit: CatalogueElementMarshaller.minimalCatalogueElementJSON(notNull(MeasurementUnit.findByName("Degrees Celsius")))]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
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
    PrimitiveType getLoadItem() {
        PrimitiveType loadItem = PrimitiveType.findByName("Primitive Test 1")
        loadItem.measurementUnit?.save(flush: true)
        loadItem
    }

    @Override
    PrimitiveType getAnotherLoadItem() {
        PrimitiveType loadItem = PrimitiveType.findByName("Primitive Test 2")
        loadItem.measurementUnit?.save(flush: true)
        loadItem
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
