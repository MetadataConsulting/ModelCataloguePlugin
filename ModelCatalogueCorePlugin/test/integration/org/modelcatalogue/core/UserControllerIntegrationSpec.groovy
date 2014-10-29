package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.security.User
import spock.lang.Ignore
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class UserControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
        [name: 'user', username: 'user', password: 'password']
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.security.User] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
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

    protected boolean getTestCreateXml() { false }

    @Override
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.@username, item.username, "username")
        checkProperty(xml.@email, item.email, "email")
        checkProperty(xml.@enabled, item.enabled, "enabled")
        checkProperty(xml.@accountExpired, item.accountExpired, "accountExpired")
        checkProperty(xml.@accountLocked, item.accountLocked, "accountLocked")
        checkProperty(xml.@passwordExpired, item.passwordExpired, "passwordExpired")
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.@username, inputItem.username, "username")
        checkProperty(xml.@email, inputItem.email, "email")
        return true
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
