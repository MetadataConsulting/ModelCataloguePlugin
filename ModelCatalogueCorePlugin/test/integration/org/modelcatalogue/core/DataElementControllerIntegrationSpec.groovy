package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
 * Created by adammilward on 27/02/2014.
 */
class DataElementControllerIntegrationSpec extends CatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", code: "AA123"]
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
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.DataElement] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        DataElement
    }

    @Override
    CatalogueElementController getController() {
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
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.code, item.code, "code")
        checkProperty(xml.@status, item.status, "status")
        checkProperty(xml.@versionNumber, item.versionNumber, "versionNumber")
        def inputItem = item.getProperty("ext")
        inputItem.each{ key, value ->
            def extension = xml.depthFirst().find{it.name()=="extension" && it.@key == key}
            checkProperty(value, extension.toString(), "extension")
        }
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.code, inputItem.code, "code")
        checkProperty(xml.@status, outputItem.status, "status")
        checkProperty(xml.@versionNumber, outputItem.versionNumber, "versionNumber")
        outputItem.getProperty("ext").each{ key, value ->
            def extension = xml.depthFirst().find{it.name()=="extension" && it.@key == key}
            checkProperty(value, extension.toString(), "extension")
        }
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkStringProperty(json.code , item.code, "code")
        checkProperty(json.status , item.status, "status")
        checkProperty(json.ext, item.ext, "extension")
        checkProperty(json.versionNumber , item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.code , inputItem.code, "code")
        checkProperty(json.status , outputItem.status, "status")
        checkMapProperty(json.ext , inputItem.ext, "extension")
        checkProperty(json.versionNumber , outputItem.versionNumber, "versionNumber")
        return true
    }

}
