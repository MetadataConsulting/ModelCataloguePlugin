package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import spock.lang.Ignore

@Ignore
class ReferenceTypeControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description ",  dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
        [name: "etTest2123", dataModel: dataModelForSpec, dataClass: CatalogueElementMarshaller.minimalCatalogueElementJSON(DataClass.findByName("book"))]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        ReferenceType
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ReferenceTypeController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    ReferenceType getLoadItem() {
        ReferenceType type = ReferenceType.findByName("Reference Test 1")
        type.dataClass?.save(flush: true)
        type
    }

    @Override
    ReferenceType getAnotherLoadItem() {
        ReferenceType type = ReferenceType.findByName("Reference Test 2")
        type.dataClass?.save(flush: true)
        type
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        assert json.dataClass
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        assert json.dataClass
        return true

    }


}
