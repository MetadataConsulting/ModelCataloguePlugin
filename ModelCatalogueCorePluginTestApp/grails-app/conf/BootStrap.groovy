import org.modelcatalogue.core.RelationshipType

class BootStrap {

    def importService

    def init = { servletContext ->

        RelationshipType.initDefaultRelationshipTypes()

       // importService.importData()

    }
    def destroy = {
    }
}
