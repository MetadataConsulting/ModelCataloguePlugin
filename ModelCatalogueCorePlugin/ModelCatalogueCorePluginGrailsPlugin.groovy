import grails.rest.render.RenderContext
import grails.util.Environment
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.marshalling.*
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer

class ModelCatalogueCorePluginGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3.7 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/index.gsp",
            "grails-app/assets/javascripts/demo.coffee"
    ]

    // TODO Fill in these fields
    def title = "Model Catalogue Core Plugin " // Headline display name of the plugin
    def author = "Adam Milward, Vladimír Oraný"
    def authorEmail = "adam.milward@outlook.com, vladimir@orany.cz"
    def description = '''\
Model catalogue core plugin (metadata registry)
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/model-catalogue-core-plugin"

    //def packaging = "binary"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "MIT"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }


    def doWithSpring = {
        mergeConfig(application)

        xlsxListRenderer(XLSXListRenderer)

        modelCatalogueCorePluginCustomObjectMarshallers(ModelCatalogueCorePluginCustomObjectMarshallers) {
            marshallers = [
                    new ConceptualDomainMarshaller(),
                    new DataElementMarshaller(),
                    new DataTypeMarshaller(),
                    new ElementsMarshaller(),
                    new ValueDomainsMarshaller(),
                    new EnumeratedTypeMarshaller(),
                    new MeasurementUnitMarshallers(),
                    new ModelMarshaller(),
                    new RelationshipTypeMarshaller(),
                    new RelationshipMarshallers(),
                    new RelationshipsMarshaller(),
                    new ValueDomainMarshaller(),
                    new MappingMarshallers(),
                    new MappingsMarshaller()
            ]
        }

    }

    def doWithDynamicMethods = { ctx ->
        ctx.grailsApplication.domainClasses.each {
            if (CatalogueElement.isAssignableFrom(it.clazz)) {
                CatalogueElementDynamicHelper.addShortcuts(it.clazz)
            }
        }
    }

    def doWithApplicationContext = { ctx ->
        //register custom json Marshallers
        //ctx.domainModellerService.modelDomains()
        ctx.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()

        XLSXListRenderer xlsxListRenderer = ctx.getBean('xlsxListRenderer')

        xlsxListRenderer.registerRowWriter {
            headers 'ID', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search'] && CatalogueElement.isAssignableFrom(container.itemType)
            } then { CatalogueElement element ->
                [[element.id, element.name, element.description]]
            }
        }

        xlsxListRenderer.registerRowWriter {
            headers 'Name', 'Source to Destination', 'Destination to Source'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search'] && RelationshipType.isAssignableFrom(container.itemType)
            } then { RelationshipType type ->
                [[type.name, type.sourceToDestination, type.destinationToSource]]
            }
        }

        xlsxListRenderer.registerRowWriter {
            headers 'Type', 'Source', 'Destination'
            when { container, context ->
                container instanceof Relationships
            } then { Relationship r ->
                [[r.relationshipType, r.source.name, r.destination.name]]
            }
        }

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    protected mergeConfig(application){
        application.config.merge(loadConfig(application))
    }

    protected loadConfig(application){
        new ConfigSlurper(Environment.current.name).parse(application.classLoader.loadClass("ModelCatalogueConfig"))
    }
}
