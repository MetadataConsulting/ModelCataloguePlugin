package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.modelcatalogue.core.util.test.TestDataHelper
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared

/**
 * Created by adammilward on 27/02/2014.
 */
abstract class AbstractIntegrationSpec extends IntegrationSpec {

    @Shared
    def fixtureLoader, fixtures, initCatalogueService, sessionFactory

    def loadMarshallers() {
        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )
        springContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
    }

    def loadFixtures(){
        TestDataHelper.initFreshDb(sessionFactory, 'testdata.sql') {
            initCatalogueService.initDefaultRelationshipTypes()
            fixtures = fixtureLoader.load("assets/*", "batches/*", "dataTypes/*", "enumeratedTypes/*", "measurementUnits/*", "models/*", "relationshipTypes/*", "classifications/*").load("actions/*", "valueDomains/*", "users/*").load("dataElements/*").load("extensions/*", "mappings/*").load("csvTransformations/*")
        }
    }

}
