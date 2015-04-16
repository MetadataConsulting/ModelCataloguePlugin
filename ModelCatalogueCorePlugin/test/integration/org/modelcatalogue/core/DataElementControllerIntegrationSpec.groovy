package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.OrderedMap

/**
 * Created by adammilward on 27/02/2014.
 */
class DataElementControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", code: "AA123", valueDomain: ValueDomain.get(1)]
    }

    @Override
    Map getNewInstance(){
       [name:"new data element", description: "the DE_author of the book", code: "12312312308"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }


    @Override
    Class getResource() {
        DataElement
    }

    @Override
    AbstractCatalogueElementController getController() {
        new DataElementController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    DataElement getLoadItem() {
        DataElement.findByName("DE_author")
    }

    @Override
    DataElement getAnotherLoadItem() {
        DataElement.findByName("DE_author1")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkStringProperty(json.modelCatalogueId , item.modelCatalogueId, "modelCatalogueId")
        checkProperty(json.status , item.status, "status")
        checkProperty(OrderedMap.fromJsonMap(json.ext), item.ext, "extension")
        checkProperty(json.versionNumber , item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.status , outputItem.status, "status")
        checkMapProperty(OrderedMap.fromJsonMap(json.ext) , inputItem.ext, "extension")
        checkProperty(json.versionNumber , outputItem.versionNumber, "versionNumber")
        return true
    }

    @Override
    protected getTotalRowsExported() { 7 }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 7, 10, 0, 7, "", ""],
                [2, 5, 5, 0, 7, "${baseLink}?max=5&offset=5", ""],
                [3, 2, 5, 5, 7, "", "${baseLink}?max=5&offset=0"],
        ]
    }

}
