package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.util.CatalogueElementFinder

/**
 * Created by adammilward on 27/02/2014.
 */
class CsvTransformationControllerIntegrationSpec extends AbstractControllerIntegrationSpec {

    def setupSpec() {
        totalCount = 1
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "Changed Transformation", columnDefinitions: [
                [source: DataElement.findByName("patient temperature uk"), destination: DataElement.findByName("patient temperature us")],
                [source: DataElement.findByName("speed of Opel"), destination: DataElement.findByName("speed of Vauxhall"), header: "speed"],
        ]]
    }

    @Override
    Map getNewInstance(){
        [name:"New Transformation",
                separator: ",",
                columnDefinitions: [
            [source: DataElement.findByName("speed of Opel"), destination: DataElement.findByName("speed of Vauxhall"), header: "speed"],
            [source: DataElement.findByName("writer")]

        ]]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.dataarchitect.CsvTransformation] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        CsvTransformation
    }

    @Override
    RestfulController getController() {
        new CsvTransformationController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    CsvTransformation getLoadItem() {
        CsvTransformation.findByName("Example")
    }


    @Override
    def customJsonPropertyCheck(item, json){
        checkProperty(json.separator , item.separator, "separator")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        checkProperty(json.separator , inputItem.separator, "separator")
        return true
    }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next, previous
                [1, 1, 10, 0, 1, "", ""],
        ]
    }


    protected getTotalRowsExported() {
        1
    }



}
