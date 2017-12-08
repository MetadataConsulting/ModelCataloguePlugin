package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.util.CatalogueElementFinder

class DataModelPolicyControllerIntegrationSpec extends AbstractControllerIntegrationSpec {

    def setupSpec() {
        totalCount = DataModelPolicy.count()
    }


    @Override
    Map getPropertiesToEdit() {
        [name: "Changed Policy", policyText: 'check dataType property "name" is unique']
    }

    @Override
    Map getNewInstance() {
        [name:"New Policy", policyText: 'check dataType property "name" is unique']
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300]
    }

    @Override
    Class getResource() {
        DataModelPolicy
    }

    @Override
    RestfulController getController() {
        new DataModelPolicyController()
    }

    @Override
    DataModelPolicy getLoadItem() {
        DataModelPolicy.findByName("Policy 1")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        checkProperty(json.name , item.name, "name")
        checkProperty(json.policyText , item.policyText, "policyText")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        checkProperty(json.name , inputItem.name, "name")
        checkProperty(json.policyText , inputItem.policyText, "policyText")
        return true
    }
}
