import grails.util.Environment
import groovy.util.logging.Log
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.reports.RegisterReportsService
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ExtensionModulesLoader
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.test.TestDataHelper

@Log
class BootStrap {

    def initCatalogueService
    def elementService
    def actionService
    def mappingService
    CatalogueBuilder catalogueBuilder
    def sessionFactory
    def modelCatalogueSearchService
    def userService
    GrailsApplication grailsApplication
    RegisterReportsService registerReportsService
    InitSecurityService initSecurityService
    MetadataSecurityService metadataSecurityService

    def init = { servletContext ->
        log.info "BootStrap:addExtensionModules()"
        ExtensionModulesLoader.addExtensionModules()
        log.info "BootStrap:addExtensionModules():complete"
        grailsApplication.domainClasses.each { GrailsDomainClass it ->
            if (CatalogueElement.isAssignableFrom(it.clazz)) {
                CatalogueElementDynamicHelper.addShortcuts(it.clazz)
            }
        }
        JSONObject.Null.metaClass.getId = {->
            null
        }

        if (Environment.current in [ Environment.TEST] && !System.getenv('MC_BLANK_DEV')) {
            TestDataHelper.initFreshDb(sessionFactory, 'initTestDatabase.sql') {
                initCatalogueService.initCatalogue(false)
                initPoliciesAndTagsService.initPoliciesAndTags()
                initSecurity(false)
                setupDevTestStuff()
            }
        } else {
            initCatalogueService.initDefaultRelationshipTypes()
            initPoliciesAndTagsService.initPoliciesAndTags()
            initSecurity(!System.getenv('MC_BLANK_DEV'))
        }

        println 'completed:initCatalogueService'
        log.info "completed:initCatalogueService"
        modelCatalogueSearchService.reindex(true)

        initCatalogueService.setupStoredProcedures()
        println 'completed:setupStoredProcedures'
        log.info "completed:setupStoredProcedures"

        if (Environment.current == Environment.PRODUCTION) {
            userService.inviteAdmins()
            println 'completed:inviteAdmins'
            log.info "completed:inviteAdmins"
        }

        //register custom json Marshallers
        //ctx.domainModellerService.modelDomains()
        grailsApplication.mainContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
        println 'completed:register'
        log.info "completed:inviteAdmins"

        registerReportsService.register()
        log.info "completed:inviteAdmins"
    }

    void initSecurity(boolean production) {
        final def var = log.info("start:initSecurity")
        initSecurityService.initRoles()

        if (!production || System.getenv("METADATA_DEMO")) {
            initSecurityService.initUsers()
            initUserRoles()
        }

        metadataSecurityService.secureUrlMappings()

        final def var1 = log.info("completed:initSecurity")
    }
    
    def setupDevTestStuff(){
        actionService.resetAllRunningActions()
        try {

            println 'Running post init job'
            println 'Finalizing all published elements'
//            CatalogueElement.findAllByStatus(ElementStatus.DRAFT).each {
//                if (it instanceof DataClass) {
//                    elementService.finalizeElement(it)
//                } else {
//                    it.status = ElementStatus.FINALIZED
//                    it.save failOnError: true
//                }
//            }


            println "Creating some actions"
            Batch batch = new Batch(name: 'Test Batch').save(failOnError: true)

            15.times {
                Action action
                if (it == 7) {
                    action = actionService.create(batch, CreateCatalogueElement, two: Action.get(2), five: Action.get(5), six: Action.get(6), name: "Model #${it}", type: DataClass.name)
                } else if (it == 4) {
                    action = actionService.create(batch, CreateCatalogueElement, two: Action.get(2), name: "Model #${it}", type: DataClass.name)
                } else {
                    action = actionService.create(batch, CreateCatalogueElement, name: "Model #${it}", type: DataClass.name)
                }
                if (it % 3 == 0) {
                    actionService.dismiss(action)
                }
            }

            assert !actionService.create(batch, TestAction, fail: true).hasErrors()
            assert !actionService.create(batch, TestAction, fail: true, timeout: 10000).hasErrors()
            assert !actionService.create(batch, TestAction, timeout: 5000, result: "the result").hasErrors()
            assert !actionService.create(batch, TestAction, test: actionService.create(batch, TestAction, fail: true, timeout: 3000)).hasErrors()


            Action createRelationshipAction = actionService.create(batch, CreateRelationship, source: MeasurementUnit.findByName("celsius"), destination: MeasurementUnit.findByName("fahrenheit"), type: RelationshipType.readByName('relatedTo'))
            if (createRelationshipAction.hasErrors()) {
                println(FriendlyErrors.printErrors("Failed to create relationship actions", createRelationshipAction.errors))
                throw new AssertionError("Failed to create relationship actions!")
            }

            setupSimpleCsvTransformation()

            DataType theType = FriendlyErrors.failFriendlySave(new DataType(name: 'data type without any data model', modelCatalogueId: 'http://www.example.com/no-data-model'))

            // for generate suggestion test
            catalogueBuilder.build {
                automatic dataType

                dataModel(name: 'Test 1') {
                    policy 'Unique of Kind'
                    dataElement(name: 'Test Element 1') {
                        dataType(name: 'Same Name')
                    }
                }

            }
            catalogueBuilder.build {
                automatic dataType
                dataModel(name: 'Test 2') {
                    policy 'Unique of Kind'
                    dataElement(name: 'Test Element 2') {
                        dataType(name: 'Same Name')
                    }
                }
            }
            catalogueBuilder.build {
                automatic dataType
                dataModel(name: 'Test 3') {
                    dataElement(name: "data element with orphaned data type")
                }
            }

            DataElement dataElement = DataElement.findByName("data element with orphaned data type")

            dataElement.dataType = theType

            FriendlyErrors.failFriendlySave(dataElement)

            println "Init finished in ${new Date()}"
        } catch (e) {
            e.printStackTrace()
        }
    }

    def setupSimpleCsvTransformation() {
        MeasurementUnit c = MeasurementUnit.findByName("celsius")
        MeasurementUnit f = MeasurementUnit.findByName("fahrenheit")

        DataType doubleType = DataType.findByName("Double")

        assert c
        assert f
        assert doubleType

        PrimitiveType temperatureUS = new PrimitiveType(name: "temperature US", measurementUnit: f, regexDef: /\d+(\.\d+)?/).save(failOnError: true)
        PrimitiveType temperature   = new PrimitiveType(name: "temperature",    measurementUnit: c, regexDef: /\d+(\.\d+)?/).save(failOnError: true)


        assert mappingService.map(temperature, temperatureUS, "(x as Double) * 9 / 5 + 32")
        assert mappingService.map(temperatureUS, temperature, "((x as Double) - 32) * 5 / 9")

        DataElement patientTemperature   = new DataElement(name: "patient temperature",    dataType: temperature).save(failOnError: true)
        DataElement patientTemperatureUS = new DataElement(name: "patient temperature US", dataType: temperatureUS).save(failOnError: true)


        CsvTransformation transformation = new CsvTransformation(name: "UK to US records").save(failOnError: true)

        new ColumnTransformationDefinition(transformation: transformation, source: DataElement.findByName("PERSON GIVEN NAME"), header: "FIRST NAME").save(failOnError: true)
        new ColumnTransformationDefinition(transformation: transformation, source: DataElement.findByName("PERSON FAMILY NAME"), header: "SURNAME").save(failOnError: true)
        new ColumnTransformationDefinition(transformation: transformation, source: patientTemperature, destination: patientTemperatureUS, header: "PATIENT TEMPERATURE").save(failOnError: true)
    }

    def destroy = {}

}
