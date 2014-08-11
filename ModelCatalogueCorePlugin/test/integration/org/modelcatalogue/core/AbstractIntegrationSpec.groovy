package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared

/**
 * Created by adammilward on 27/02/2014.
 */
abstract class AbstractIntegrationSpec extends IntegrationSpec {

    @Shared
    def fixtureLoader, fixtures, initCatalogueService

    def loadMarshallers() {
        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )
        springContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
    }

    def loadFixtures(){
        if(CatalogueElement.count()==0){
            fixtures = fixtureLoader.load("assets/*", "batches/*", "dataTypes/*", "enumeratedTypes/*", "measurementUnits/*", "dataElements/*", "conceptualDomains/*", "models/*", "relationshipTypes/*").load("actions/*", "valueDomains/*", "extensions/*")
        }
        initCatalogueService.initDefaultRelationshipTypes()
    }

}
