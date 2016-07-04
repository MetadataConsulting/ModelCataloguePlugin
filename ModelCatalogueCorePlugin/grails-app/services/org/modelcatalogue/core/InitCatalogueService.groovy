package org.modelcatalogue.core

import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.io.support.Resource
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.TestDataHelper
import org.modelcatalogue.integration.mc.ModelCatalogueLoader

class InitCatalogueService {

    static transactional = false

    def grailsApplication
    def dataModelService
    def elementService
    def sessionFactory
    def cacheService

    def initCatalogue(boolean test = false){
        Closure init = {
            initDefaultRelationshipTypes()
            initDefaultDataTypes(test)
        }
        if (test) {
            TestDataHelper.initFreshDb(sessionFactory, 'initCatalogue.sql', init)
        } else {
            init()
        }
    }

    def initDefaultDataTypes(boolean failOnError = false) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver()

        List<Resource> forSecondPass = []

        // first pass
        for (Resource resource in resolver.getResources('classpath*:**/*.mc')) {
            if (resource.file.absolutePath.contains('/test/')) {
                continue
            }
            try {
                readMCFile(resource, true)
            } catch (Exception e) {
                log.info("Resource $resource couldn't be processed at the moment, will try again later", e)
                forSecondPass << resource
            }
        }


        // second pass
        for (Resource resource in forSecondPass) {
            readMCFile(resource, failOnError)
        }
    }

    private void readMCFile(Resource resource, boolean failOnError) {
        try {
            log.info "Importing MC file ${resource.URI}"
            Set<CatalogueElement> lastCreated = importMCFile(resource.inputStream, true)
            for (CatalogueElement element in lastCreated) {
                if (element.status == ElementStatus.DRAFT) {
                    elementService.finalizeElement(element)
                }
            }
            log.info "File ${resource.URI} imported"
        } catch (e) {
            if (failOnError) {
                throw new IllegalArgumentException("Exception parsing model catalogue file ${resource.URI}", e)
            } else {
                log.error("Exception parsing model catalogue file ${resource.URI}", e)
            }
        }
    }

    def initDefaultRelationshipTypes() {
        cacheService.clearCache()
        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.relationshiptypes

        for (Map definition in defaultDataTypes) {
            RelationshipType existing = RelationshipType.readByName(definition.name)
            if (!existing) {
                RelationshipType type = new RelationshipType(definition)
                type.save(failOnError: true, flush: true)

                if (type.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Cannot create relationship type $definition.name", type.errors))
                }
            } else if (grailsApplication.config.mc.sync.relationshipTypes) {
                existing.properties = definition
                FriendlyErrors.failFriendlySave(existing)
            } else {
                if (definition.rule && definition.rule.replaceAll(/\s+/, ' ').trim() != existing.rule?.replaceAll(/\s+/, ' ')?.trim()) {
                    log.warn("""
                    Your current rule for relationship type '${existing.name}' is different than the one from configuration. This may cause unexpected behaviour:
                    ===EXPECTED'${existing.name}'===
                    ${definition.rule?.trim()}
                    ===ACTUAL '${existing.name}'===
                    ${existing.rule?.trim()}
                """.stripIndent())

                }
                if (!existing.sourceToDestinationDescription && definition.sourceToDestinationDescription) {
                    existing.sourceToDestinationDescription = definition.sourceToDestinationDescription
                    existing.save(failOnError: true)
                }
                if (!existing.destinationToSourceDescription && definition.destinationToSourceDescription) {
                    existing.destinationToSourceDescription = definition.destinationToSourceDescription
                    existing.save(failOnError: true)
                }
            }
        }
    }

    Set<CatalogueElement> importMCFile(InputStream inputStream, boolean skipDraft = false, DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)) {
        if (skipDraft) {
            builder.skip ElementStatus.DRAFT
        }

        ModelCatalogueLoader.build(builder)
                .classLoader(grailsApplication.classLoader)
                .blackList(GormStaticApi)
                .create().load(inputStream)

        builder.created
    }

}
