import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.reports.RegisterReportDescriptorsService
import org.modelcatalogue.core.security.*
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ExtensionModulesLoader
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.test.TestDataHelper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.modelcatalogue.core.TestUtil

class BootStrap {
    def grailsResourceLocator
    def initCatalogueService
    def elementService
    def actionService
    CatalogueBuilder catalogueBuilder
    def sessionFactory
    def modelCatalogueSearchService
    def userService
    GrailsApplication grailsApplication
    RegisterReportDescriptorsService registerReportDescriptorsService
    InitSecurityService initSecurityService
    MetadataSecurityService metadataSecurityService
    InitPoliciesAndTagsService initPoliciesAndTagsService
    UserGormService userGormService
    DataModelAclService dataModelAclService
    DataModelGormService dataModelGormService

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

        initCatalogueService.setupStoredProcedures()
        log.info "completed:setupStoredProcedures"

        grailsApplication.mainContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
        log.info 'completed:registerMarshallers'

        log.info 'init roles'
        initSecurityService.initRoles()

        log.info 'init role hierarchy'
        initSecurityService.initRoleHierarchyEntry()

        if ( isDev() ) {
            initDev()

        } else if ( isTest() ) {
            initTest()

        }  else if ( isProduction() ) {
            initProd()
            log.info 'init register reports'
            registerReportDescriptorsService.register()
        }
    }


    void initEmptyDataBase() {
        log.info 'Setting up an empty Database'

        log.info 'init default relationship types'
        initCatalogueService.initDefaultRelationshipTypes()

        log.info 'init policies and tags'
        initPoliciesAndTagsService.initPoliciesAndTags()

        log.info 'init users'
        initSecurityService.initUsers()

        log.info 'init user roles'
        initSecurityService.initUserRoles()

        log.info 'init request maps'
        metadataSecurityService.secureUrlMappings()

        log.info 'configure acl'
        loginAs('curator')
        configureAcl()

        if ( !skipReindex() ) {
            log.info 'init reindexing catalogue'
            modelCatalogueSearchService.reindex(true)
        }

        log.info 'init register reports'
        registerReportDescriptorsService.register()


    }

    private void configureAcl() {
        List<DataModel> dataModelList = DataModel.findAll()
        for ( DataModel dataModel : dataModelList ) {
            dataModelAclService.addAdministrationPermission(dataModel)
            if (removeAclDuplicates()) {
                dataModelAclService.removeDuplicatedPermissions(dataModel)
            }
        }
    }

    private void loginAs(String username, String authority = MetadataRoles.ROLE_ADMIN) {
        User user = userGormService.findByUsername(username)
        if ( user ) {
            // have to be authenticated as an admin to create ACLs
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(authority)
            SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(user.username,
                    user.password,
                    authorityList)
        }

    }

    void initProd() {
        if ( isBlankDev() ) {
            initEmptyDataBase()
            log.info 'init register reports'
        } else {
            log.info 'init request maps'
            metadataSecurityService.secureUrlMappings()

            loginAs('supervisor')
            configureAcl()
        }
    }

    void initDev() {
        initEmptyDataBase()
    }

    void initTest() {

        if ( isBlankDev() ) {
            initEmptyDataBase()

        } else {
            TestDataHelper.initFreshDb(sessionFactory, 'initTestDatabase.sql') {

                log.info 'init catalogue'
                initCatalogueService.initCatalogue(false)

                log.info 'init policies and tags'
                initPoliciesAndTagsService.initPoliciesAndTags()

                log.info 'init roles'
                initSecurityService.initRoles()

                log.info 'init role hierarchy'
                initSecurityService.initRoleHierarchyEntry()

                log.info 'init users'
                initSecurityService.initUsers()

                log.info 'init user roles'
                initSecurityService.initUserRoles()

                log.info 'init request maps'
                metadataSecurityService.secureUrlMappings()

                log.info 'setting up dev test stuff'
                setupDevTestStuff()

                log.info 'configure acl'
                loginAs('curator')
                configureAcl()

                DataModel dataModel = DataModel.findByName('Cancer Model')
                dataModelAclService.addReadPermission(dataModel, 'user')

            }
            if ( !skipReindex() ) {
                log.info 'init reindexing catalogue'
                modelCatalogueSearchService.reindex(true)
            }
            log.info 'init register reports'
            registerReportDescriptorsService.register()
        }
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

    boolean skipReindex() {
        String str = System.getenv('SKIP_REINDEX')
        if (str) {
            return Boolean.valueOf(str)
        }
        return false
    }

    boolean removeAclDuplicates() {
        String str = System.getenv('REMOVE_ACL_DUPLICATES')
        if (str) {
            return Boolean.valueOf(str)
        }
        return false
    }


    boolean isBlankDev() {
        String str = System.getenv('MC_BLANK_DEV')
        if (str) {
            return Boolean.valueOf(str)
        }
        return false
    }

    boolean isDemo() {
        String str = System.getenv('METADATA_DEMO')
        if (str) {
            return Boolean.valueOf(str)
        }
        return false
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
