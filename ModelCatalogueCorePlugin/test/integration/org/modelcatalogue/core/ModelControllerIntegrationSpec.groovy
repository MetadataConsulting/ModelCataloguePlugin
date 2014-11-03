package org.modelcatalogue.core

import grails.converters.JSON
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Created by adammilward on 27/02/2014.
 */
class ModelControllerIntegrationSpec extends AbstractPublishedElementControllerIntegrationSpec {

    protected boolean getRecord() {
        true
    }

    def setupSpec(){
        totalCount = 12
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
       [name:"new model", description: "the model of the book"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    Class getResource() {
        Model
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ModelController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Model getLoadItem() {
        Model.findByName("mTest3")
    }

    @Override
    Model getAnotherLoadItem() {
        Model.findByName("mTest4")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkStatusProperty(json.status , item.status, "status")
        checkProperty(json.versionNumber , item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkStatusProperty(json.status , outputItem.status, "status")
        checkProperty(json.versionNumber , outputItem.versionNumber, "versionNumber")
        return true
    }

    @Override
    protected getTotalRowsExported() { 5 }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 5, 10, 0, 5, "", ""],
                [2, 5, 5, 0, 5, "", ""],
        ]
    }

    def "new Test for json"(){

        def x = new Model(name:"test").save(flush:true)

        when:

//        def string2 = '{"modelCatalogueId":" ' + x.modelCatalogueId + '", "name":"adam123123","description":"test","version":0,"elementType":"org.modelcatalogue.core.Model","dateCreated":"2014-08-15T13:02:27.000Z","lastUpdated":"2014-08-15T13:02:27.000Z","link":"/model/ ' + x.id + '","availableReports":[{"title":"Export All Elements of test to XML","url":"/ModelCatalogueCorePluginTestApp/api/modelCatalogue/core/dataArchitect/getSubModelElements?format=xml&asset=true&name=Export+All+Elements+of+test+to+XML&id=182","type":"ASSET"},{"title":"","url":"/ModelCatalogueCorePluginTestApp/api/modelCatalogue/core/model?asset=true&name=","type":"ASSET"}],"ext":{},"versionNumber":1,"status":"DRAFT","versionCreated":"2014-08-15T13:02:27.000Z","defaultExcludes":["id",""elementType","incomingRelationships","outgoingRelationships","link","mappings"],"updatableProperties":["modelCatalogueId","archived","name","description","version","dateCreated","lastUpdated","relationships","hasContextOf","childOf","hasAttachmentOf","contains","parentOf","relatedTo","availableReports","ext","versionNumber","status","versionCreated","history"],"__enhancedBy":["catalogueElement"]}'

        def string2 = '{"modelCatalogueId":"' + x.defaultModelCatalogueId + '","archived":false,"name":"adam123123","description":"test","version":0,"elementType":"org.modelcatalogue.core.Model","dateCreated":"2014-08-15T13:02:27.000Z","lastUpdated":"2014-08-15T13:02:27.000Z","link":"/model/182","availableReports":[{"title":"Export All Elements of test to XML","url":"/ModelCatalogueCorePluginTestApp/api/modelCatalogue/core/dataArchitect/getSubModelElements?format=xml&asset=true&name=Export+All+Elements+of+test+to+XML&id=182","type":"ASSET"},{"title":"","url":"/ModelCatalogueCorePluginTestApp/api/modelCatalogue/core/model?asset=true&name=","type":"ASSET"}],"ext":{},"versionNumber":1,"status":"DRAFT","versionCreated":"2014-08-15T13:02:27.000Z","defaultExcludes":["id","elementType","incomingRelationships","outgoingRelationships","link","mappings"],"updatableProperties":["modelCatalogueId","archived","name","description","version","dateCreated","lastUpdated","relationships","hasContextOf","childOf","hasAttachmentOf","contains","parentOf","relatedTo","availableReports","ext","versionNumber","status","versionCreated","history"],"__enhancedBy":["catalogueElement"]}'

        controller.params.id  = x.id
        controller.request.method = "PUT"
        controller.request.format = "json"
        controller.request.content = string2.getBytes()
        controller.request.makeAjaxRequest()

        controller.update()

        def response2 = controller.response.json

        then:
        response2.name == "adam123123"



    }

}
