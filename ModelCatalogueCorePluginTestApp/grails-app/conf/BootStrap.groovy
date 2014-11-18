import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.testapp.Requestmap
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.http.HttpMethod

class BootStrap {

    def importService
    def domainModellerService
    def initCatalogueService
    def elementService
    def executorService
    def actionService
    def mappingService

    XLSXListRenderer xlsxListRenderer
    ReportsRegistry reportsRegistry

    def init = { servletContext ->

        initCatalogueService.initCatalogue()

        def roleUser = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
        def roleAdmin = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
        def metadataCurator = Role.findByAuthority('ROLE_METADATA_CURATOR') ?: new Role(authority: 'ROLE_METADATA_CURATOR').save(failOnError: true)

        def admin   = User.findByName('admin') ?: new User(name: 'admin', username: 'admin', enabled: true, password: 'A!6m1n2014').save(failOnError: true)
        def viewer  = User.findByName('viewer') ?: new User(name: 'viewer', username: 'viewer', enabled: true, password: 'v13w3r').save(failOnError: true)
        def curator = User.findByName('curator') ?: new User(name: 'curator', username: 'curator', enabled: true, password: 'c2r4t0r').save(failOnError: true)


        if (!admin.authorities.contains(roleAdmin)) {
            UserRole.create admin, roleUser
            UserRole.create admin, metadataCurator
            UserRole.create admin, roleAdmin, true
        }

        if (!curator.authorities.contains(metadataCurator)) {
            UserRole.create curator, roleUser
            UserRole.create curator, metadataCurator
        }

        if (!viewer.authorities.contains(viewer)) {
            UserRole.create viewer, roleUser
        }

        //permit all for assets and initial pages
        for (String url in [
                '/',
                '/**/favicon.ico',
                '/fonts/**',
                '/assets/**',
                '/plugins/**/js/**',
                '/js/vendor/**',
                '/**/*.less',
                '/**/js/**',
                '/**/css/**',
                '/**/images/**',
                '/**/img/**',
                '/login', '/login.*', '/login/*',
                '/logout', '/logout.*', '/logout/*',
                '/register/*', '/errors', '/errors/*'
        ]) {
            createRequestmapIfMissing(url, 'permitAll', null)
        }

        createRequestmapIfMissing('/asset/download/*',             'IS_AUTHENTICATED_ANONYMOUSLY', org.springframework.http.HttpMethod.GET)
        createRequestmapIfMissing('/user/current',                 'IS_AUTHENTICATED_ANONYMOUSLY', org.springframework.http.HttpMethod.GET)
        createRequestmapIfMissing('/catalogue/*/**',               'IS_AUTHENTICATED_ANONYMOUSLY', org.springframework.http.HttpMethod.GET)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'IS_AUTHENTICATED_ANONYMOUSLY', org.springframework.http.HttpMethod.GET)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'ROLE_METADATA_CURATOR',        org.springframework.http.HttpMethod.POST)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'ROLE_METADATA_CURATOR',        org.springframework.http.HttpMethod.PUT)
        createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'ROLE_METADATA_CURATOR',        org.springframework.http.HttpMethod.DELETE)


        createRequestmapIfMissing('/console/**',                   'ROLE_ADMIN')
        createRequestmapIfMissing('/dbconsole/**',                 'ROLE_ADMIN')
        createRequestmapIfMissing('/plugins/console-1.5.0/**',     'ROLE_ADMIN')

//        createRequestmapIfMissing('/api/modelCatalogue/core/model/**', 'IS_AUTHENTICATED_ANONYMOUSLY')
//        createRequestmapIfMissing('/api/modelCatalogue/core/dataElement/**', 'ROLE_METADATA_CURATOR')
//        createRequestmapIfMissing('/api/modelCatalogue/core/dataType/**', 'ROLE_USER')
//        createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'ROLE_METADATA_CURATOR')
//        createRequestmapIfMissing('/api/modelCatalogue/core/relationshipTypes/**', 'ROLE_ADMIN')



        environments {
            development {
                setupStuff()

            }
            test {
                setupStuff()
            }

        }

    }

    def setupStuff(){
        actionService.resetAllRunningActions()
        try {
            println 'Running post init job'
            println 'Importing data'
            importService.importData()

            println 'Finalizing all published elements'
            CatalogueElement.findAllByStatusNotEqual(ElementStatus.FINALIZED).each {
                if (it instanceof Model) {
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
                    action = actionService.create(batch, CreateCatalogueElement, two: Action.get(2), five: Action.get(5), six: Action.get(6), name: "Model #${it}", type: Model.name)
                } else if (it == 4) {
                    action = actionService.create(batch, CreateCatalogueElement, two: Action.get(2), name: "Model #${it}", type: Model.name)
                } else {
                    action = actionService.create(batch, CreateCatalogueElement, name: "Model #${it}", type: Model.name)
                }
                if (it % 3 == 0) {
                    actionService.dismiss(action)
                }
            }

            def parent = new Model(name:"parent1", status: ElementStatus.FINALIZED).save(flush:true)
            parent.addToChildOf(parent)

            assert !actionService.create(batch, TestAction, fail: true).hasErrors()
            assert !actionService.create(batch, TestAction, fail: true, timeout: 10000).hasErrors()
            assert !actionService.create(batch, TestAction, timeout: 5000, result: "the result").hasErrors()
            assert !actionService.create(batch, TestAction, test: actionService.create(batch, TestAction, fail: true, timeout: 3000)).hasErrors()


            Action createRelationshipAction = actionService.create(batch, CreateRelationship, source: MeasurementUnit.findByName("celsius"), destination: MeasurementUnit.findByName("fahrenheit"), type: RelationshipType.findByName('relatedTo'))
            if (createRelationshipAction.hasErrors()) {
                println createRelationshipAction.errors
                throw new AssertionError("Failed to create relationship actions!")
            }


            setupSimpleCsvTransformation()

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

        ValueDomain temperatureUS = new ValueDomain(name: "temperature US", dataType: doubleType, unitOfMeasure: f, regexDef: /\d+(\.\d+)?/).save(failOnError: true)
        ValueDomain temperature   = new ValueDomain(name: "temperature",    dataType: doubleType, unitOfMeasure: c, regexDef: /\d+(\.\d+)?/).save(failOnError: true)


        assert mappingService.map(temperature, temperatureUS, "(x as Double) * 9 / 5 + 32")
        assert mappingService.map(temperatureUS, temperature, "((x as Double) - 32) * 5 / 9")

        DataElement patientTemperature   = new DataElement(name: "patient temperature",    valueDomain: temperature).save(failOnError: true)
        DataElement patientTemperatureUS = new DataElement(name: "patient temperature US", valueDomain: temperatureUS).save(failOnError: true)


        CsvTransformation transformation = new CsvTransformation(name: "UK to US records").save(failOnError: true)

        new ColumnTransformationDefinition(transformation: transformation, source: DataElement.findByName("PERSON GIVEN NAME"), header: "FIRST NAME").save(failOnError: true)
        new ColumnTransformationDefinition(transformation: transformation, source: DataElement.findByName("PERSON FAMILY NAME"), header: "SURNAME").save(failOnError: true)
        new ColumnTransformationDefinition(transformation: transformation, source: patientTemperature, destination: patientTemperatureUS, header: "PATIENT TEMPERATURE").save(failOnError: true)
    }

    def destroy = {}


    private static Requestmap createRequestmapIfMissing(String url, String configAttribute, HttpMethod method = null) {
        Requestmap.findOrSaveByUrlAndConfigAttributeAndHttpMethod(url, configAttribute, method, [failOnError: true])
    }

}
