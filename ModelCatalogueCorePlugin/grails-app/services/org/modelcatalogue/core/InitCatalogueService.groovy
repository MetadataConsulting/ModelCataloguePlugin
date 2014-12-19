package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.io.support.Resource
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.core.util.builder.CatalogueBuilderScript

@Transactional
class InitCatalogueService {

    def grailsApplication
    def classificationService
    def elementService

    def initCatalogue(boolean failOnError = false){
        initDefaultRelationshipTypes()
        initDefaultDataTypes(failOnError)
    }

    def initDefaultDataTypes(boolean failOnError = false) {
        CatalogueBuilder builder = new CatalogueBuilder(classificationService, elementService)
        GroovyShell shell = prepareGroovyShell(builder)
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver()

        for (Resource resource in resolver.getResources('classpath*:**/*.mc')) {
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
                    throw e
                }
                log.error("Exception parsing model catalogue file ${resource.URI}", e)
            }
        }
    }

    def initDefaultRelationshipTypes() {

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.relationshiptypes

        for (Map definition in defaultDataTypes) {
            RelationshipType existing = RelationshipType.findByName(definition.name)
            if (!existing) {
                RelationshipType type = new RelationshipType(definition)
                type.save()

                if (type.hasErrors()) {
                    log.error("Cannot create relationship type $definition.name. $type.errors")
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
