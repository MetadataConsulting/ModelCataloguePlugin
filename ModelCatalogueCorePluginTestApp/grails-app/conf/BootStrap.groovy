import grails.plugin.springsecurity.acl.AclService
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.persistence.RequestmapGormService
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.security.*
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ExtensionModulesLoader
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Metadata
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.modelcatalogue.core.util.test.TestDataHelper
import groovy.util.logging.Log

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
    AclService aclService
    ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy
    RequestmapGormService requestmapGormService
    MetadataSecurityService metadataSecurityService

    def init = { servletContext ->

        if ( Environment.current == Environment.TEST ) {
            // See: https://github.com/grails-plugins/grails-rest-client-builder/issues/40
            RestBuilder.metaClass.constructor = { ->
                def constructor = RestBuilder.class.getConstructor()
                def instance = constructor.newInstance()
                instance.restTemplate.messageConverters.removeAll {
                    it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter'
                }
                instance
            }
        }

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
                initPoliciesAndTags()
                initSecurity(false)
                setupDevTestStuff()
            }
        } else {
            initCatalogueService.initDefaultRelationshipTypes()
            initPoliciesAndTags()
            initSecurity(!System.getenv('MC_BLANK_DEV'))
        }

        println 'completed:initCatalogueService'
        log.info "completed:initCatalogueService"
        //modelCatalogueSearchService.reindex(true)

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
        println 'completed:registeringMarshallers'

        grailsApplication.mainContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()
        println 'completed:register'

        log.info "completed:inviteAdmins"

        ReportsRegistry reportsRegistry = grailsApplication.mainContext.getBean(ReportsRegistry)
        setUpReports(reportsRegistry)
        log.info "completed:inviteAdmins"

        loginAsSupervisor()
        println 'completed:loginAsSuperVisor'
        log.info "completed:loginAsSuperVisor"

        configureAcl()
        println 'completed:configuredAcl'
        log.info "completed:configuredAcl"
    }

    private void configureAcl() {
        List<DataModel> dataModelList = DataModel.findAll()
        for ( DataModel dataModel : dataModelList ) {
            ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy.getObjectIdentity(dataModel)
            try {
                Acl acl = aclService.readAclById(objectIdentity)
                if ( !acl ) {
                    aclService.createAcl(objectIdentity)
                }
            } catch ( NotFoundException e ) {
                log.warn "NotFoundException for ${dataModel.id}"
                //aclService.createAcl(objectIdentity)
            }

        }
    }

    private void loginAsSupervisor() {
        String authority = 'ROLE_SUPERVISOR'
        List<User> users = UserRole.findAllByRole(Role.findByAuthority(authority))?.user
        if ( users ) {
            User user = users.get(0)
            // have to be authenticated as an admin to create ACLs
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(authority)
            SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(user.username,
                    user.password,
                    authorityList)
        }

    }

    private static void setUpReports(ReportsRegistry reportsRegistry){
        reportsRegistry.register {
            creates asset
            title { "Export All Elements of ${it.name} to Excel XSLX" }
            defaultName { "Export All Elements of ${it.name} to Excel XSLX" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.countContains() > 0
            }
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'NHIC'], id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'inventorySpreadsheet', id: true
        }

//        reportsRegistry.register {
//            creates asset
//            title { "Grid Report Spreadsheet" }
//            defaultName { "${it.name} report as MS Excel Document Grid" }
//            depth 3
//            type DataModel
//            when { DataModel dataModel ->
//                dataModel.countDeclares() > 0
//            }
//            link controller: 'dataModel', action: 'gridSpreadsheet', id: true
//        }

        reportsRegistry.register {
            creates asset
            title { "MC Excel Export" }
            defaultName { "${it.name} report as MS Excel Document Grid" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'excelExporterSpreadsheet', id: true
        }

        List<String> northThamesHospitalNames = ['GOSH', 'LNWH', 'MEH', 'UCLH'] // not sure if this should be defined here. Maybe it would be better in a source file, or perhaps a config file.
        List<String> gelSourceModelNames = ['Cancer Model', 'Rare Diseases']
        northThamesHospitalNames.each{ name ->
            reportsRegistry.register {
                creates asset
                title { "GMC Grid Report – North Thames – ${name}" }
                defaultName { "${it.name} report as MS Excel Document Grid" }
                depth 7 // for Rare Diseases
                type DataModel
                when { DataModel dataModel ->
                    (dataModel.countDeclares() > 0) && (gelSourceModelNames.contains(dataModel.name))
                }
                link controller: 'northThames', action: 'northThamesSummaryReport', id: true, params: [organization: name]
            }
        }


        reportsRegistry.register {
            creates asset
            title { "Inventory Report Document" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventoryDoc', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Document" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'inventoryDoc', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventorySpreadsheet', id: true
        }

//  needs more work
//        reportsRegistry.register {
//            creates asset
//            title { "Changelog Document" }
//            defaultName { "${it.name} changelog as MS Word Document" }
//            depth 3
//            includeMetadata true
//            type DataClass
//            link controller: 'dataClass', action: 'changelogDoc', id: true
//        }

        reportsRegistry.register {
            creates link
            type DataModel, DataClass, DataElement, DataType, MeasurementUnit
            title { "Export to Catalogue XML" }
            link { CatalogueElement element ->
                [url: element.getDefaultModelCatalogueId(false) + '?format=xml']
            }
        }

        reportsRegistry.register {
            creates link
            title { "Generate all ${it.name} files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_CANCER_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllCancerReports', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Generate all ${it.name} files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllRareDiseaseReports', id: true
        }


        reportsRegistry.register {
            creates link
            title { "Rare Diseases Disorder List (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseDisorderListAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria Report (Word Doc)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Phenotypes and Clinical Tests Report (Word Doc)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc', id: true
        }


        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Phenotypes Split Docs" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseSplitDocs', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Disorder List Only (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseListAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOEligibilityCriteriaAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Disease Eligibility Criteria Report (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Static Website" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get('Rare Disease Report Available') == 'true' || dataModel.ext.get("All Rare Disease Conditions Reports") == 'true' || dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true' || dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasesWebsite', id: true
        }



        reportsRegistry.register {
            creates link
            title { "Rare Diseases Static Website" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get('Rare Disease Report Available') == 'true' || dataModel.ext.get("All Rare Disease Conditions Reports") == 'true' || dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true' || dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasesWebsite', id: true
        }
    }

    private void initSecurity(boolean production) {
        final def var = log.info("start:initSecurity")
        def roleUser = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
        def roleAdmin = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
        def roleSupervisor = Role.findByAuthority('ROLE_SUPERVISOR') ?: new Role(authority: 'ROLE_SUPERVISOR').save(failOnError: true)
        def roleStacktrace = Role.findByAuthority('ROLE_STACKTRACE') ?: new Role(authority: 'ROLE_STACKTRACE').save(failOnError: true)
        def metadataCurator = Role.findByAuthority('ROLE_METADATA_CURATOR') ?: new Role(authority: 'ROLE_METADATA_CURATOR').save(failOnError: true)

        Role.findByAuthority('ROLE_REGISTERED') ?: new Role(authority: 'ROLE_REGISTERED').save(failOnError: true)

        if (!production || System.getenv("METADATA_DEMO")) {
            def supervisor = User.findByNameOrUsername('supervisor', 'supervisor') ?: new User(name: 'supervisor', username: 'supervisor', enabled: true, password: System.getenv('MC_SUPERVISOR_PASSWORD') ?: 'supervisor', email: System.getenv(UserService.ENV_SUPERVISOR_EMAIL), apiKey: 'supervisorabcdef123456').save(failOnError: true)
            def admin = User.findByNameOrUsername('admin', 'admin') ?: new User(name: 'admin', username: 'admin', enabled: true, password: 'admin', email: System.getenv('MC_ADMIN_EMAIL'), apiKey: 'adminabcdef123456').save(failOnError: true)
            def viewer = User.findByNameOrUsername('viewer', 'viewer') ?: new User(name: 'viewer', username: 'viewer', enabled: true, password: 'viewer', apiKey: 'viewerabcdef123456').save(failOnError: true)
            def curator = User.findByNameOrUsername('curator', 'curator') ?: new User(name: 'curator', username: 'curator', enabled: true, password: 'curator', apiKey: 'curatorabcdef123456').save(failOnError: true)
            User.findByNameOrUsername('registered', 'registered') ?: new User(name: 'registered', username: 'registered', enabled: true, password: 'registered', apiKey: 'registeredabcdef123456').save(failOnError: true)


            if (!supervisor.authorities.contains(roleSupervisor)) {
                UserRole.create supervisor, roleUser
                UserRole.create supervisor, metadataCurator
                UserRole.create supervisor, roleStacktrace
                UserRole.create supervisor, roleAdmin
                UserRole.create supervisor, roleSupervisor
            }

            if (!admin.authorities.contains(roleAdmin)) {
                UserRole.create admin, roleUser
                UserRole.create admin, metadataCurator
                UserRole.create admin, roleStacktrace
                UserRole.create admin, roleAdmin
            }

            if (!curator.authorities.contains(metadataCurator)) {
                UserRole.create curator, roleUser
                UserRole.create curator, metadataCurator
            }

            if (!viewer.authorities.contains(viewer)) {
                UserRole.create viewer, roleUser
            }
        }

        metadataSecurityService.secureUrlMappings()

        final def var1 = log.info("completed:initSecurity")
    }

    def initPoliciesAndTags() {
        log.info "start:initPoliciesAndTags"

        catalogueBuilder.build {
            dataModelPolicy(name: 'Unique of Kind', overwrite: true) {
                check dataClass property 'name' is 'unique'
                check dataElement property 'name' is 'unique'
                check dataType property 'name' is 'unique'
                check validationRule property 'name' is 'unique'
                check measurementUnit property 'name' is 'unique'
                check ModelCatalogueTypes.CATALOGUE_ELEMENT property 'modelCatalogueId' is 'unique'
            }
            dataModelPolicy(name: 'Default Checks') {
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#authors' is 'required' otherwise 'Metadata "Authors" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#reviewers' is 'required' otherwise 'Metadata "Reviewers" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#owner' is 'required' otherwise 'Metadata "Owner" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#reviewed' is 'required' otherwise 'Metadata "Reviewed" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#approved' is 'required' otherwise 'Metadata "Approved" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#namespace' is 'required' otherwise 'Metadata "Namespace" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#organization' is 'required' otherwise 'Metadata "Organization" is missing for {2}'

                check dataElement property 'dataType' is 'required' otherwise 'Data type is missing for {2}'
                check dataElement property 'name' is 'unique' otherwise 'Data element\'s name is not unique for {2}'
                check dataType property 'name' is 'unique' otherwise 'Data type\'s name is not unique for {2}'
                check dataType property 'name' apply regex: /[^_ -]+/ otherwise 'Name of {2} contains illegal characters ("_", "-" or " ")'
            }
            dataModel(name: 'Clinical Tags') {
                tag(name: 'Highly Sensitive PI data')
                tag(name: 'Sensitive PI data')
                tag(name: 'Highly Sensitive data')
                tag(name: 'Sensitive data')
                tag(name: 'Internal data')
                tag(name: 'External data')
            }
            log.info "complete:initPoliciesAndTags"
        }
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




    protected static mergeConfig(GrailsApplication application){
        application.config.merge(loadConfig(application))
    }

    protected static loadConfig(GrailsApplication application){
        new ConfigSlurper(Environment.current.name).parse(application.classLoader.loadClass("ModelCatalogueConfig"))
    }



    def static getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return null
    }

    def static getParentModel(DataElement dataElement){
        DataClass containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    def static getUnitOfMeasure(DataElement dataElement){
        if (dataElement.dataType && dataElement.dataType instanceof PrimitiveType) {
            return dataElement.dataType.measurementUnit?.name
        }
        return null
    }

    def static getUnitOfMeasureSymbol(DataElement dataElement){
        if (dataElement.dataType && dataElement.dataType instanceof PrimitiveType) {
            return dataElement.dataType.measurementUnit?.symbol
        }
        return null
    }

    def static getDataType(DataElement dataElement){
        DataType dataType = dataElement.dataType
        if (!dataType) {
            return ''
        }
        if (dataType instanceof EnumeratedType) {
            return dataType.enumerations.collect { key, value -> "$key:$value"}.join('\n')
        }
        return dataType.name

    }

    def static getDataModelsString(CatalogueElement dataElement) {
        if (!dataElement?.dataModel) {
            return ""
        }
        dataElement.dataModel.name
    }

    def static getEnumerationString(DataType dataType){
        if (dataType instanceof EnumeratedType) {
            return dataType.prettyPrint()
        }
        return null
    }

}
