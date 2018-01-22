package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.CatalogueElementFinder

/**
 * Created by adammilward on 27/02/2014.
 */
class RelationshipTypeControllerIntegrationSpec extends AbstractControllerIntegrationSpec {

    def setupSpec() {
        totalCount = RelationshipType.count()
    }

    def "return list of catalogue element classes in json"() {
        controller.response.format = 'json'

        controller.elementClasses()
        def json = controller.response.json

        expect:
        json.size() == CatalogueElementFinder.catalogueElementClasses.size()
    }

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", sourceClass: CatalogueElement, destinationClass: CatalogueElement]
    }

    @Override
    Map getNewInstance() {
        [name:"NewInstanceAntonym",
                sourceToDestination: "NewInstanceAntonymWith",
                destinationToSource: "NewInstanceAntonymWith",
                sourceClass: DataElement,
                destinationClass: DataElement]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, sourceToDestination: "NewInstanceAntonymWith", destinationToSource: "NewInstanceAntonymWith", sourceClass: DataElement, destinationClass: DataElement]
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

    def getPaginationParameters(String baseLink) {
        int total = RelationshipType.countBySystem(false)
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, total, "${baseLink}?max=10&total=${total}&offset=10", ""],
                [2, 5, 5, 0, total, "${baseLink}?max=5&total=${total}&offset=5", ""],
                [3, 5, 5, 5, total, "${baseLink}?max=5&total=${total}&offset=10", "${baseLink}?max=5&total=${total}&offset=0"],
                [4, 4, 4, 8, total, "${baseLink}?max=4&total=${total}&offset=12", "${baseLink}?max=4&total=${total}&offset=4"],
                [5, RelationshipType.countBySystem(false) - 10, 10, 10, total, "", "${baseLink}?max=10&total=${total}&offset=0"],
                [6, 2, 2, 10, total, "${baseLink}?max=2&total=${total}&offset=12", "${baseLink}?max=2&total=${total}&offset=8"]
        ]
    }


}
