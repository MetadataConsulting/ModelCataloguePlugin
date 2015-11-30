package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

class UserControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance(){
        [name: 'user', username: 'user', password: 'password', dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t" * 300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        User
    }

    @Override
    AbstractCatalogueElementController getController() {
        new UserController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    User getLoadItem() {
        User.findByName("Adam")
    }

    @Override
    User getAnotherLoadItem() {
        User.findByName("Bella")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkProperty(json.username, item.username, "username")
        checkProperty(json.email, item.email, "email")
        checkProperty(json.enabled, item.enabled, "enabled")
        checkProperty(json.accountExpired, item.accountExpired, "accountExpired")
        checkProperty(json.accountLocked, item.accountLocked, "accountLocked")
        checkProperty(json.passwordExpired, item.passwordExpired, "passwordExpired")

        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.username, inputItem.username, "username")
        checkProperty(json.email, inputItem.email, "email")

        return true
    }

    boolean isCheckVersion() {
        false
    }

}
