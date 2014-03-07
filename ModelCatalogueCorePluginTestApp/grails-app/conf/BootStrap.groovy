import org.modelcatalogue.core.RelationshipType

class BootStrap {

    def importService

    def init = { servletContext ->

        RelationshipType.initDefaultRelationshipTypes()

        environments {
            development {
                importService.importData()
            }
        }

    }
    def destroy = {
    }
}
