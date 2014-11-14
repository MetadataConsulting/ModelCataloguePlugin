package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.io.support.Resource
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.util.CatalogueBuilder
import org.modelcatalogue.core.util.CatalogueBuilderScript

@Transactional
class InitCatalogueService {

    def grailsApplication
    def classificationService
    def elementService

    def initCatalogue(){
        initDefaultRelationshipTypes()
        initDefaultDataTypes()
    }

    def initDefaultDataTypes() {
        CatalogueBuilder builder = new CatalogueBuilder(classificationService)

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


        GroovyShell shell = new GroovyShell(grailsApplication.classLoader, new Binding(builder: builder), configuration)
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver()

        for (Resource resource in resolver.getResources('classpath*:**/*.mc')) {
            try {
                shell.evaluate(resource.URI)
                for (CatalogueElement element in builder.lastCreated) {
                    elementService.finalizeElement(element)
                }
            } catch (e) {
                log.error("Exception parsing model catalogue file", e)
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


}
