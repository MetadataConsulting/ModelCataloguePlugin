package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.io.support.Resource
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.util.CatalogueBuilder
import org.modelcatalogue.core.util.CatalogueBuilderScript

@Transactional
class InitCatalogueService {

//    modelcatalogue.defaults.datatypes = [
//
//    [name: "String", description: "java.lang.String"],
//    [name: "Integer", description: "java.lang.Integer"],
//    [name: "Double", description: "java.lang.Double"],
//    [name: "Boolean", description: "java.lang.Boolean"],
//    [name: "Date", description: "java.util.Date"],
//    [name: "Time", description: "java.sql.Time"],
//    [name: "Currency", description: "java.util.Currency"],
//    [name: "Text", description: "a text field"],
//
//    ]

    def grailsApplication
    def classificationService
    def elementService

    def initCatalogue(){
        initDefaultRelationshipTypes()
        initDefaultDataTypes()
        initDefaultMeasurementUnits()
    }

    def initDefaultDataTypes() {
        CatalogueBuilder builder = new CatalogueBuilder(classificationService)

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = CatalogueBuilderScript.name
        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer()
        secureASTCustomizer.with {
            packageAllowed = false
            indirectImportCheckEnabled = true

            importsWhitelist = [Object.name]
            starImportsWhitelist = [Object.name]
            staticImportsWhitelist = [Object.name]
            staticStarImportsWhitelist = [Object.name]

            receiversClassesBlackList = [System, GormStaticApi]
        }
        configuration.addCompilationCustomizers(secureASTCustomizer)


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


    def initDefaultMeasurementUnits() {

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.measurementunits

        for (Map definition in defaultDataTypes) {
            MeasurementUnit existing = MeasurementUnit.findByName(definition.name)
            if (!existing) {
                MeasurementUnit unit = new MeasurementUnit(definition)
                unit.save()

                if (unit.hasErrors()) {
                    log.error("Cannot create measurement unit $definition.name. $unit.errors")
                }
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
