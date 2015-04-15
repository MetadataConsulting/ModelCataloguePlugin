package org.modelcatalogue.core

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.io.support.Resource
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.core.util.builder.CatalogueBuilderScript
import org.modelcatalogue.core.util.test.TestDataHelper

class InitCatalogueService {

    static transactional = false

    def grailsApplication
    def classificationService
    def elementService
    def sessionFactory

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
        CatalogueBuilder builder = new CatalogueBuilder(classificationService, elementService)
        builder.skip ElementStatus.DRAFT

        GroovyShell shell = prepareGroovyShell(builder)
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver()

        List<Resource> forSecondPass = []

        // first pass
        for (Resource resource in resolver.getResources('classpath*:**/*.mc')) {
            try {
                readMCFile(resource, shell, builder, true)
            } catch (Exception e) {
                log.info("Resource $resource couldn't be processed at the moment, will try again later", e)
                forSecondPass << resource
            }
        }


        // second pass
        for (Resource resource in forSecondPass) {
            readMCFile(resource, shell, builder, failOnError)
        }
    }

    private void readMCFile(Resource resource, GroovyShell shell, CatalogueBuilder builder, boolean failOnError) {
        try {
            log.info "Importing MC file ${resource.URI}"
            shell.evaluate(resource.inputStream.newReader())
            for (CatalogueElement element in builder.lastCreated) {
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

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.relationshiptypes

        for (Map definition in defaultDataTypes) {
            RelationshipType existing = RelationshipType.readByName(definition.name)
            if (!existing) {
                RelationshipType type = new RelationshipType(definition)
                type.save(failOnError: true, flush: true)

                if (type.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Cannot create relationship type $definition.name", type.errors))
                }
            } else {
                if (definition.rule && definition.rule.replaceAll(/\s+/, ' ').trim() != existing.rule?.replaceAll(/\s+/, ' ')?.trim()) {
                    log.warn("""
                    Your current rule for relationship type '${existing.name}' is different than the one from configuration. This may cause unexpected behaviour:
                    ===EXPECTED'${existing.name}'===
                    ${definition.rule.trim()}
                    ===ACTUAL '${existing.name}'===
                    ${existing.rule.trim()}
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

    Set<CatalogueElement> importMCFile(InputStream inputStream) {
        CatalogueBuilder builder = new CatalogueBuilder(classificationService, elementService)
        GroovyShell shell = prepareGroovyShell(builder)
        shell.evaluate(inputStream.newReader())
        builder.lastCreated
    }

    private GroovyShell prepareGroovyShell(CatalogueBuilder builder) {
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = CatalogueBuilderScript.name

        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer()
        secureASTCustomizer.with {
            packageAllowed = false
            indirectImportCheckEnabled = true

            importsWhitelist = [Object.name, CatalogueBuilder.name]
            starImportsWhitelist = [Object.name, CatalogueBuilder.name]
            staticImportsWhitelist = [Object.name, CatalogueBuilder.name]
            staticStarImportsWhitelist = [Object.name, CatalogueBuilder.name]

            receiversClassesBlackList = [System, GormStaticApi]
        }
        configuration.addCompilationCustomizers secureASTCustomizer


        new GroovyShell(grailsApplication.classLoader, new Binding(builder: builder), configuration)
    }


}
