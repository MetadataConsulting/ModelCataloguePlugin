import grails.rest.render.RenderContext
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.actions.CreateCatalogueElement
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.testapp.Requestmap
import org.modelcatalogue.core.testapp.UserRole
import org.modelcatalogue.core.testapp.Role
import org.modelcatalogue.core.testapp.User
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.TestAction

class BootStrap {

    def importService
    def domainModellerService
    def initCatalogueService
    def publishedElementService
    def executorService
    def actionService

    XLSXListRenderer xlsxListRenderer
    ReportsRegistry reportsRegistry

    def init = { servletContext ->

        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
        initCatalogueService.initDefaultMeasurementUnits()

        xlsxListRenderer.registerRowWriter('reversed') {
            title "Reversed DEMO Export"
            append metadata
            headers 'Description', 'Name', 'ID'
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search'] && container.itemType && CatalogueElement.isAssignableFrom(container.itemType)
            } then { CatalogueElement element ->
                [[element.description, element.name, element.id]]
            }
        }

        def roleUser = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
        def roleAdmin = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
        def metadataCurator = Role.findByAuthority('ROLE_METADATA_CURATOR') ?: new Role(authority: 'ROLE_METADATA_CURATOR').save(failOnError: true)

        def admin   = User.findByUsername('admin') ?: new User(username: 'admin', enabled: true, password: 'admin').save(failOnError: true)
        def viewer  = User.findByUsername('viewer') ?: new User(username: 'viewer', enabled: true, password: 'viewer').save(failOnError: true)
        def curator = User.findByUsername('curator') ?: new User(username: 'curator', enabled: true, password: 'curator').save(failOnError: true)


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
            new Requestmap(url: url, configAttribute: 'permitAll').save(failOnError: true)
        }

        new Requestmap(url: '/api/modelCatalogue/core/*/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY',   httpMethod: org.springframework.http.HttpMethod.GET).save(failOnError: true)
        new Requestmap(url: '/asset/download/*',             configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY',   httpMethod: org.springframework.http.HttpMethod.GET).save(failOnError: true)
        new Requestmap(url: '/api/modelCatalogue/core/*/**', configAttribute: 'ROLE_METADATA_CURATOR',          httpMethod: org.springframework.http.HttpMethod.POST).save(failOnError: true)
        new Requestmap(url: '/api/modelCatalogue/core/*/**', configAttribute: 'ROLE_METADATA_CURATOR',          httpMethod: org.springframework.http.HttpMethod.PUT).save(failOnError: true)
        new Requestmap(url: '/api/modelCatalogue/core/*/**', configAttribute: 'ROLE_METADATA_CURATOR',          httpMethod: org.springframework.http.HttpMethod.DELETE).save(failOnError: true)

//        new Requestmap(url: '/api/modelCatalogue/core/model/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save(failOnError: true)
//        new Requestmap(url: '/api/modelCatalogue/core/dataElement/**', configAttribute: 'ROLE_METADATA_CURATOR').save(failOnError: true)
//        new Requestmap(url: '/api/modelCatalogue/core/dataType/**', configAttribute: 'ROLE_USER').save(failOnError: true)
//        new Requestmap(url: '/api/modelCatalogue/core/*/**', configAttribute: 'ROLE_METADATA_CURATOR').save(failOnError: true)
//        new Requestmap(url: '/api/modelCatalogue/core/relationshipTypes/**', configAttribute: 'ROLE_ADMIN').save(failOnError: true)



        environments {
            development {
                try {
                    println 'Running post init job'
                    println 'Importing data'
                    importService.importData()
                    def classification =  new Classification(name: "dataSet1").save(failOnError: true)
//                    def de = new DataElement(name: "testera", description: "test data architect", classifications: [classification]).save(failOnError: true)
//                    de.ext.metadata = "test metadata"
//
//                    println 'Creating dummy models'
//                    15.times {
//                        new Model(name: "Another root #${String.format('%03d', it)}").save(failOnError: true)
//                    }
//
//                    def parentModel1 = Model.findByName("Another root #001")
//
//                    15.times{
//                        def child = new Model(name: "Another root #${String.format('%03d', it)}").save(failOnError: true)
//                        parentModel1.addToParentOf(child)
//                    }
//
//
//
//
//                    for (DataElement element in DataElement.list()) {
//                        parentModel1.addToContains element
//                        classification.addToClassifies(element)
//                    }
//
//
                    println 'Finalizing all published elements'
                    PublishedElement.findAllByStatusNotEqual(PublishedElementStatus.FINALIZED).each {
                        if (it instanceof Model) {
                            publishedElementService.finalizeTree(it)
                        } else {
                            it.status = PublishedElementStatus.FINALIZED
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

                    def parent = new Model(name:"parent1", status: PublishedElementStatus.FINALIZED).save(flush:true)
                    parent.addToChildOf(parent)

                    assert !actionService.create(batch, TestAction, fail: true).hasErrors()
                    assert !actionService.create(batch, TestAction, fail: true, timeout: 10000).hasErrors()
                    assert !actionService.create(batch, TestAction, timeout: 5000, result: "the result").hasErrors()
                    assert !actionService.create(batch, TestAction, test: actionService.create(batch, TestAction, fail: true, timeout: 3000)).hasErrors()

                    println "Init finished in ${new Date()}"
                } catch (e) {
                    e.printStackTrace()
                }
                //domainModellerService.modelDomains()
            }
        }

    }

    def destroy = {}

}
