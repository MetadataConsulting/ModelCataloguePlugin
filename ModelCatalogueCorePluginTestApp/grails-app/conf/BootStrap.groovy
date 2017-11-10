import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.reports.RegisterReportsService
import org.modelcatalogue.core.security.InitSecurityService
import org.modelcatalogue.core.security.MetadataSecurityService
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ExtensionModulesLoader
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.test.TestDataHelper
import org.modelcatalogue.core.security.InitSecurityService
import org.modelcatalogue.core.security.MetadataSecurityService

class BootStrap {

    def initCatalogueService
    def elementService
    def actionService
    CatalogueBuilder catalogueBuilder
    def sessionFactory
    def modelCatalogueSearchService
    def userService
    GrailsApplication grailsApplication
    RegisterReportsService registerReportsService
    InitSecurityService initSecurityService
    MetadataSecurityService metadataSecurityService
    InitPoliciesAndTagsService initPoliciesAndTagsService
    SetupSimpleCsvTransformationService setupSimpleCsvTransformationService

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

        if (isTest() && !isBlankDev()) {
            TestDataHelper.initFreshDb(sessionFactory, 'initTestDatabase.sql') {
                initCatalogueService.initCatalogue(false)
                initPoliciesAndTagsService.initPoliciesAndTags()
                log.info("start:initSecurity")
                initSecurityService.initRoles()
                initSecurityService.initUsers()
                metadataSecurityService.secureUrlMappings()
                log.info("completed:initSecurity")
                setupDevTestStuff()
            }
        } else {
            initCatalogueService.initDefaultRelationshipTypes()
            initPoliciesAndTagsService.initPoliciesAndTags()
            log.info("start:initSecurity")
            initSecurityService.initRoles()
            if (isBlankDev() || isDemo()) {
                initSecurityService.initUsers()
            }
            metadataSecurityService.secureUrlMappings()
            log.info("completed:initSecurity")
        }

        log.info 'completed:initCatalogueService'
        modelCatalogueSearchService.reindex(true)

        initCatalogueService.setupStoredProcedures()
        log.info "completed:setupStoredProcedures"

        if ( isProduction() ) {
            userService.inviteAdmins()
            log.info "completed:inviteAdmins"
        }

        grailsApplication.mainContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
        log.info 'completed:registerMarshallers'

        registerReportsService.register()
        log.info "completed:inviteAdmins"
    }

    boolean isDev() {
        Environment.current == Environment.DEVELOPMENT
    }

    boolean isTest() {
        Environment.current == Environment.TEST
    }

    boolean isProduction() {
        Environment.current == Environment.PRODUCTION
    }

    boolean isBlankDev() {
        System.getenv('MC_BLANK_DEV') as boolean
    }

    boolean isDemo() {
        System.getenv('METADATA_DEMO') as boolean
    }

    def setupDevTestStuff(){
        actionService.resetAllRunningActions()
        try {

            log.info 'Running post init job'
            log.info 'Finalizing all published elements'
//            CatalogueElement.findAllByStatus(ElementStatus.DRAFT).each {
//                if (it instanceof DataClass) {
//                    elementService.finalizeElement(it)
//                } else {
//                    it.status = ElementStatus.FINALIZED
//                    it.save failOnError: true
//                }
//            }


            log.info "Creating some actions"
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
                log.error(FriendlyErrors.printErrors("Failed to create relationship actions", createRelationshipAction.errors))
                throw new AssertionError("Failed to create relationship actions!")
            }

            setupSimpleCsvTransformationService.setupSimpleCsvTransformation()

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

            log.info "Init finished in ${new Date()}"
        } catch (e) {
            e.printStackTrace()
        }
    }

    def destroy = {}

}
