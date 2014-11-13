class ModelCatalogueSecuritySs2GrailsPlugin {
    // the plugin version
    def version = "0.9.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Model Catalogue Spring Security 2.x Plugin" // Headline display name of the plugin
    def author = "Adam Milward, Vladimír Oraný"
    def authorEmail = "adam.milward@outlook.com, vladimir@orany.cz"
    def description = '''\
Spring Security 2.x implementation of Model Catalogue Core Plugin abstract security service.
'''


    // URL to the plugin's documentation
    def documentation = "https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/javadoc/guide/"

    //def packaging = "binary"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "MIT"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    def issueManagement = [ system: "GitHub", url: "https://github.com/MetadataRegistry/ModelCataloguePlugin/issues" ]

    def scm = [ url: "https://github.com/MetadataRegistry/ModelCataloguePlugin" ]


    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        springConfig.addAlias('modelCatalogueSecurityService','springSecurity2SecurityService')
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
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
