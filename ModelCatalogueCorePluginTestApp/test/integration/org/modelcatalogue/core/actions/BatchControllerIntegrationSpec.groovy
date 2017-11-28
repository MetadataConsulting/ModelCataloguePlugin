package org.modelcatalogue.core.actions

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.AbstractControllerIntegrationSpec

class BatchControllerIntegrationSpec extends AbstractControllerIntegrationSpec {

    def setupSpec() {
        totalCount = 6
    }

    @Override
    Map getPropertiesToEdit() {
        [name: "New Name", archived: Boolean.TRUE]
    }

    @Override
    Map getNewInstance() {
        [name:"New Batch", archive: Boolean.FALSE]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300]
    }

    @Override
    Class getResource() {
        Batch
    }

    @Override
    RestfulController getController() {
        new BatchController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Batch getLoadItem() {
        Batch.findByName("Generic Batch")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        // checkProperty(json.archived , item.archived, "archived")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        // checkProperty(json.archived , inputItem.archived, "archived")
        return true
    }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max, off, tot,                         next,                     previous
                [   1,   6,  10,   0,   6,                           "",                           ""],
                [   2,   5,   5,   0,   6, "${baseLink}?max=5&offset=5",                           ""],
                [   3,   1,   5,   5,   6,                           "", "${baseLink}?max=5&offset=0"],
                [   6,   2,   2,   2,   6, "${baseLink}?max=2&offset=4", "${baseLink}?max=2&offset=0"]
        ]
    }

}
