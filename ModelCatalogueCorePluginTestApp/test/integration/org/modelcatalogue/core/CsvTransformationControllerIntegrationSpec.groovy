package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.CsvTransformation

/**
 * Created by adammilward on 27/02/2014.
 */
class CsvTransformationControllerIntegrationSpec extends AbstractControllerIntegrationSpec {

    def setupSpec() {
        totalCount = 1
    }

    @Override
    Map getPropertiesToEdit() {
        [name: "Changed Transformation", columns: [
                [source: DataElement.findByName("patient temperature uk"), destination: DataElement.findByName("patient temperature us")],
                [source: DataElement.findByName("speed of Opel"), destination: DataElement.findByName("speed of Vauxhall"), header: "speed"],
        ]]
    }

    @Override
    Map getNewInstance() {
        [name:"New Transformation",
            columns: [
            [source: DataElement.findByName("speed of Opel"), destination: DataElement.findByName("speed of Vauxhall"), header: "speed"],
            [source: DataElement.findByName("writer")]

        ]]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300]
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
        assert json.columns
        assert json.columns.size() == 4
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        assert json.columns
        assert json.columns.size() == 2
        return true
    }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next, previous
                [1, 1, 10, 0, 1, "", ""],
        ]
    }


}
