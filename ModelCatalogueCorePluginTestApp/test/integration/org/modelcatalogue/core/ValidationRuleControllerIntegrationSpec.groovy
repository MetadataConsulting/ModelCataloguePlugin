package org.modelcatalogue.core

import grails.util.GrailsNameUtils

class ValidationRuleControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description", dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
       [name:"new validation rule", description: "for validating something", dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t"*300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        ValidationRule
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ValidationRuleController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    ValidationRule getLoadItem() {
        ValidationRule.findByName("rule1")
    }

    @Override
    ValidationRule getAnotherLoadItem() {
        ValidationRule.findByName("rule2")
    }

}
