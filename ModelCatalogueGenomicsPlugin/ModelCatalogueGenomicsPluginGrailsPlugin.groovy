import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.reports.ReportsRegistry

class ModelCatalogueGenomicsPluginGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Model Catalogue Genomics Plugin" // Headline display name of the plugin
    def author = "Vladimir Orany"
    def authorEmail = "vladimir@orany.cz"
    def description = '''\
Genomics England customisation plugin for Model Catalogue
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/model-catalogue-genomics-plugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

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

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
        ReportsRegistry reportsRegistry = ctx.getBean(ReportsRegistry)

        reportsRegistry.register {
            creates asset
            title { "GEL Data Specification Report" }
            defaultName { "${it.name} report as MS Word Document" }
            type DataModel
            link controller: 'genomics', action: 'exportGelSpecification', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Rare Diseases HPO And Clinical Tests Json" }
            defaultName { "${it.name} report as Json" }
            type DataClass
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTests', id: true
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
}
