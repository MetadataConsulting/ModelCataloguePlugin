
class ModelCatalogueCorePluginBootstrap {

    def domainModellerService

    def init = { servletContext ->

        domainModellerService.modelDomains()
        println('test')

    }


    def destroy = {
    }
}