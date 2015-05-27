import org.modelcatalogue.core.util.ExtensionModulesLoader

class BootStrap {

    def initCatalogueService

    def init = { servletContext ->
        ExtensionModulesLoader.addExtensionModules()
        initCatalogueService.initDefaultRelationshipTypes()
    }
}
