import grails.rest.render.RenderContext
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.testapp.Requestmap
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ExtensionModulesLoader
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.test.TestDataHelper
import org.springframework.http.HttpMethod

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

    def init = { servletContext ->
        ExtensionModulesLoader.addExtensionModules()

        grailsApplication.domainClasses.each { GrailsDomainClass it ->
            if (CatalogueElement.isAssignableFrom(it.clazz)) {
                CatalogueElementDynamicHelper.addShortcuts(it.clazz)
            }
        }
        JSONObject.Null.metaClass.getId = {->
            null
        }

        if (Environment.current in [Environment.DEVELOPMENT, Environment.TEST] && !System.getenv('MC_BLANK_DEV')) {
            TestDataHelper.initFreshDb(sessionFactory, 'initTestDatabase.sql') {
                initCatalogueService.initCatalogue(true)
                initPoliciesAndTags()
                initSecurity(false)
                setupDevTestStuff()
            }

        } else {
            initCatalogueService.initDefaultRelationshipTypes()
            initSecurity(!System.getenv('MC_BLANK_DEV'))
        }

        modelCatalogueSearchService.reindex(true).all { it }.toBlocking().subscribe {
            System.out.println "Reindexed"
        }

        initCatalogueService.setupStoredProcedures()

        if (Environment.current == Environment.PRODUCTION) {
            userService.inviteAdmins()
        }


        //register custom json Marshallers
        //ctx.domainModellerService.modelDomains()
        grailsApplication.mainContext.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()

        ReportsRegistry reportsRegistry = grailsApplication.mainContext.getBean(ReportsRegistry)

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
            defaultName { "${it.name} report as MS Excel Document Inventory" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'inventorySpreadsheet', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Grid Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document Grid" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'gridSpreadsheet', id: true
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


// needs work before we can release
//        reportsRegistry.register {
//            creates link
//            title { "Change Log for RD Phenotypes And Clinical Tests (Excel)" }
//            type DataModel
//            when { DataModel dataModel ->
//                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
//            }
//            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsXls', id: true
//        }
// needs work before we can release
//        reportsRegistry.register {
//            creates link
//            title { "Change Log for RD Eligibility (Excel)" }
//            type DataModel
//            when { DataModel dataModel ->
//                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
//            }
//            link controller: 'genomics', action: 'exportRareDiseaseEligibilityChangeLogAsXls', id: true
//        }
//// needs work before we can release
//        reportsRegistry.register {
//            creates link
//            title { "GEL Data Specification Change Log (Excel)" }
//            type DataModel
//            link controller: 'genomics', action: 'exportDataSpecChangeLogAsXls', id: true
//        }
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

// needs more work
//        reportsRegistry.register {
//            creates asset
//            title { "GEL Changelog (Word Doc)" }
//            defaultName { "${it.name} changelog as MS Word Document" }
//            depth 3
//            includeMetadata true
//            type DataModel
//            when { DataModel dataModel ->
//                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
//            }
//            link controller: 'genomics', action: 'exportChangeLogDocument', id: true
//        }

//        reportsRegistry.register {
//            creates asset
//            title { "GEL Data Specification Report (Word Doc)" }
//            defaultName { "${it.name} report as MS Word Document" }
//            depth 3
//            type DataModel
//            link controller: 'genomics', action: 'exportGelSpecification', id: true
//        }



    }

    private static void initSecurity(boolean production) {
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
                UserRole.create supervisor, roleSupervisor, true
            }

            if (!admin.authorities.contains(roleAdmin)) {
                UserRole.create admin, roleUser
                UserRole.create admin, metadataCurator
                UserRole.create admin, roleStacktrace
                UserRole.create admin, roleAdmin, true
            }

            if (!curator.authorities.contains(metadataCurator)) {
                UserRole.create curator, roleUser
                UserRole.create curator, metadataCurator
            }

            if (!viewer.authorities.contains(viewer)) {
                UserRole.create viewer, roleUser
            }
        }

        //permit all for assets and initial pages
        for (String url in [
                '/',
                '/**/favicon.ico',
                '/fonts/**',
                '/stomp/**',
                '/assets/**',
                '/plugins/**/js/**',
                '/plugins/jquery-ui-*/**',
                '/js/vendor/**',
                '/**/*.less',
                '/**/js/**',
                '/**/css/**',
                '/**/images/**',
                '/**/img/**',
                '/login', '/login.*', '/login/*',
                '/logout', '/logout.*', '/logout/*',
                '/register/*', '/errors', '/errors/*',
                '/load',
                '/index.gsp'
        ]) {
            createRequestmapIfMissing(url, 'permitAll', null)
        }

        createRequestmapIfMissing('/asset/download/*',                      'isAuthenticated()',   HttpMethod.GET)
        createRequestmapIfMissing('/oauth/*/**',                            'IS_AUTHENTICATED_ANONYMOUSLY')
        createRequestmapIfMissing('/user/current',                          'IS_AUTHENTICATED_ANONYMOUSLY',  HttpMethod.GET)
        createRequestmapIfMissing('/catalogue/upload',                      'ROLE_METADATA_CURATOR',         HttpMethod.POST)
        createRequestmapIfMissing('/catalogue/*/**',                        'isAuthenticated()',   HttpMethod.GET)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',   HttpMethod.GET)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/*/comments',  'isAuthenticated()',   HttpMethod.POST) // post a comment
        createRequestmapIfMissing('/api/modelCatalogue/core/user/*/favourite', 'isAuthenticated()',HttpMethod.POST) // favourite item
        createRequestmapIfMissing('/api/modelCatalogue/core/user/apikey',    'isAuthenticated()',HttpMethod.POST) // get or create new api key
        createRequestmapIfMissing('/api/modelCatalogue/core/user/*/favourite', 'isAuthenticated()',HttpMethod.DELETE) // unfavourite item
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'ROLE_METADATA_CURATOR',         HttpMethod.POST)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'ROLE_METADATA_CURATOR',         HttpMethod.PUT)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'ROLE_METADATA_CURATOR',         HttpMethod.DELETE)
        createRequestmapIfMissing('/api/modelCatalogue/core/asset/*/validateXML',  'isAuthenticated()',   HttpMethod.POST) // validate xml

        createRequestmapIfMissing('/sso/*/**',                              'isAuthenticated()',   HttpMethod.GET)

        createRequestmapIfMissing('/role/**',                               'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/userAdmin/**',                          'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/requestMap/**',                         'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/registrationCode/**',                   'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/securityInfo/**',                       'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/console/**',                            'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/plugins/console*/**',                   'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/dbconsole/**',                          'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/monitoring/**',                         'ROLE_SUPERVISOR')
        createRequestmapIfMissing('/plugins/console-1.5.0/**',              'ROLE_SUPERVISOR')

    }

    def initPoliciesAndTags() {
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
                tag(name: 'Registration, consent and demographic data essential for the management of the participant')
                tag(name: 'Sample tracking data essential for processing and tracking the sample from collection through to sequencing')
                tag(name: 'Clinical data essential for diagnostics purposes')
                tag(name: 'Clinical data essential for research')
                tag(name: 'Clinical data for research')
                tag(name: 'Highly Sensitive PI data')
                tag(name: 'Sensitive PI data')
                tag(name: 'Highly Sensitive data')
                tag(name: 'Sensitive data')
                tag(name: 'Internal data')
                tag(name: 'External data')
            }
        }
    }

    def setupDevTestStuff(){
        actionService.resetAllRunningActions()
        try {

            println 'Running post init job'
            println 'Finalizing all published elements'
            CatalogueElement.findAllByStatus(ElementStatus.DRAFT).each {
                if (it instanceof DataClass) {
                    elementService.finalizeElement(it)
                } else {
                    it.status = ElementStatus.FINALIZED
                    it.save failOnError: true
                }
            }


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
                    dataElement(name: 'Test Element 2') {
                        dataType(name: 'Same Name')
                    }
                }

            }

            catalogueBuilder.build {
                dataModel(name: 'Test 2') {
                    automatic dataType
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


    private static Requestmap createRequestmapIfMissing(String url, String configAttribute, HttpMethod method = null) {
        List<Requestmap> maps = Requestmap.findAllByUrlAndHttpMethod(url, method)
        for(Requestmap map in maps) {
            if (map.configAttribute == configAttribute) {
                return map
            }
            println "Requestmap method: $method, url: $url has different config attribute - expected: $configAttribute, actual: $map.configAttribute"
        }
        Requestmap.findOrSaveByUrlAndConfigAttributeAndHttpMethod(url, configAttribute, method, [failOnError: true])
    }

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
