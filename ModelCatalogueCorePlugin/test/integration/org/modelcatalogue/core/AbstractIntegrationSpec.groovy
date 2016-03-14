package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.modelcatalogue.core.util.test.TestDataHelper
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared

abstract class AbstractIntegrationSpec extends IntegrationSpec {

    def fixtureLoader
    def fixtures
    def initCatalogueService
    def sessionFactory
    def relationshipTypeService

    void loadMarshallers() {
        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )
        springContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
    }


    void initRelationshipTypes(){
        TestDataHelper.initFreshDb(sessionFactory, 'reltypes.sql') {
            initCatalogueService.initDefaultRelationshipTypes()
        }
        relationshipTypeService.clearCache()
    }

    void initCatalogue(){
        initCatalogueService.initCatalogue(true)
        relationshipTypeService.clearCache()
    }

    void loadFixtures(){
        TestDataHelper.initFreshDb(sessionFactory, 'testdata.sql') {
            initCatalogueService.initDefaultRelationshipTypes()
            fixtures = fixtureLoader.load("assets/*", "batches/*", "dataTypes/*", "enumeratedTypes/*", "measurementUnits/*", "models/*", "relationshipTypes/*", "classifications/*").load("actions/*", "users/*", "referenceTypes/*", "primitiveTypes/*").load("dataElements/*").load("extensions/*", "mappings/*").load("csvTransformations/*")
        }
        relationshipTypeService.clearCache()
    }

    public <T> T notNull(T item) {
        assert item
        item
    }

}
