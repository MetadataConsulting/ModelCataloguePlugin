package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils

/**
 * Created by adammilward on 27/02/2014.
 */
class RelationshipTypeControllerIntegrationSpec extends AbstractControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", sourceClass: PublishedElement, destinationClass: PublishedElement]
    }

    @Override
    Map getNewInstance(){
        [name:"NewInstanceAntonym",
                sourceToDestination: "NewInstanceAntonymWith",
                destinationToSource: "NewInstanceAntonymWith",
                sourceClass: DataElement,
                destinationClass: DataElement]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, sourceToDestination: "NewInstanceAntonymWith", destinationToSource: "NewInstanceAntonymWith", sourceClass: DataElement, destinationClass: DataElement]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.RelationshipType] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] exceeds the maximum size of [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        RelationshipType
    }

    @Override
    RestfulController getController() {
        new RelationshipTypeController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    RelationshipType getLoadItem() {
        RelationshipType.findByName("Antonym")
    }


    @Override
    def xmlCustomPropertyCheck(xml, item){
    //    super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.sourceToDestination, item.sourceToDestination, "sourceToDestination")
        checkProperty(xml.destinationToSource, item.destinationToSource, "destinationToSource")
        checkProperty(xml.sourceClass, item.sourceClass.name, "destinationToSource")
        checkProperty(xml.destinationClass, item.destinationClass.name, "destinationToSource")
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
  //      super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.sourceToDestination, inputItem.sourceToDestination, "sourceToDestination")
        checkProperty(xml.destinationToSource, inputItem.destinationToSource, "destinationToSource")
        checkProperty(xml.sourceClass, inputItem.sourceClass.name, "destinationToSource")
        checkProperty(xml.destinationClass, inputItem.destinationClass.name, "destinationToSource")
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
//        super.customJsonPropertyCheck(item, json)
        checkProperty(json.sourceToDestination , item.sourceToDestination, "sourceToDestination")
        checkProperty(json.destinationToSource , item.destinationToSource, "sourceToDestination")
        checkProperty(json.sourceClass , item.sourceClass.name, "sourceClass")
        checkProperty(json.destinationClass , item.destinationClass.name, "destinationClass")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
  //      super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.sourceToDestination , inputItem.sourceToDestination, "sourceToDestination")
        checkProperty(json.destinationToSource , inputItem.destinationToSource, "sourceToDestination")
        checkProperty(json.sourceClass , inputItem.sourceClass.name, "sourceClass")
        checkProperty(json.destinationClass , inputItem.destinationClass.name, "destinationClass")
        return true
    }

}
