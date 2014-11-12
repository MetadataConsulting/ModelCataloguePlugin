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
//    [name: "String", description: "java.lang.String"],
//    [name: "Integer", description: "java.lang.Integer"],
//    [name: "Double", description: "java.lang.Double"],
//    [name: "Boolean", description: "java.lang.Boolean"],
//    [name: "Date", description: "java.util.Date"],
//    [name: "Time", description: "java.sql.Time"],
//    [name: "Currency", description: "java.util.Currency"],
//    [name: "Text", description: "a text field"],

//    [name: "xs:normalizedString", description: "A string that does not contain line feeds, carriage returns, or tabs"],
//    [name: "xs:byte" , description: "A signed 8-bit integer"],
//    [name: "xs:int", description: "A signed 32-bit integer"],
//    [name: "xs:integer", description: "An integer value"],
//    [name: "xs:long", description: "A signed 64-bit integer"],
//    [name: "xs:token", description: " A token is the set of strings that do not contain the carriage return (#xD), line feed (#xA) nor tab (#x9) characters, that have no leading or trailing spaces (#x20) and that have no internal sequences of two or more spaces."],
//    [name: "xs:nonPositiveInteger", description: "Defines a nonPositive integer"],
//    [name: "xs:negativeInteger", description: "Defines a negative integer"],
//    [name: "xs:short", description: "Defines an integer value between minInclusive -32768 to maxInclusive 32767"],
//    [name: "xs:nonNegativeInteger", description: "Defines an integer value with minInclusive to 0"],
//    [name: "xs:unsignedLong", description: "Defines an nonNegativeInteger value with maxInclusive to 18446744073709551615"],
//    [name: "xs:unsignedInt", description: "Defines an unsignedLong value with maxInclusive to 4294967295"],
//    [name: "xs:unsignedShort", description: "Defines an unsignedInt value with maxInclusive to 65535"],
//    [name: "xs:unsignedByte", description: "Defines an unsignedShort value with maxInclusive to 255"],
//    [name: "xs:positiveInteger", description: "Defines a nonNegativeInteger value with minInclusive to 1"]
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
