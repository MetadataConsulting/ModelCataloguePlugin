import org.modelcatalogue.core.RelationshipType

class BootStrap {

    def importService
    def domainModellerService

    def init = { servletContext ->

        RelationshipType.initDefaultRelationshipTypes()

       // importService.importData()
       // domainModellerService.modelDomains()

    }
    def destroy = {
    }
}
