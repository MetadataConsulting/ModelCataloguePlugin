package org.modelcatalogue.testapp

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
        relationshipTypeService.clearCache()
        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )
        springContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
    }


    void initRelationshipTypes(){
        relationshipTypeService.clearCache()
        TestDataHelper.initFreshDb(sessionFactory, 'reltypes.sql') {
            initCatalogueService.initDefaultRelationshipTypes()
        }
    }

    void initCatalogue(){
        relationshipTypeService.clearCache()
        TestDataHelper.initFreshDb(sessionFactory, 'initcatalogue.sql') {
            initCatalogueService.initCatalogue(true)
        }
    }

    void loadFixtures(){
        relationshipTypeService.clearCache()
        TestDataHelper.initFreshDb(sessionFactory, 'testdata.sql') {
            initCatalogueService.initDefaultRelationshipTypes()
            fixtures = fixtureLoader.load("assets/*", "batches/*", "dataTypes/*", "enumeratedTypes/*", "measurementUnits/*", "models/*", "relationshipTypes/*", "classifications/*").load("actions/*", "users/*", "referenceTypes/*", "primitiveTypes/*").load("dataElements/*").load("extensions/*", "mappings/*").load("csvTransformations/*")
        }
    }

    public <T> T notNull(T item) {
        assert item
        item
    }

}
