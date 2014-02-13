import uk.co.brc.modelcatalogue.ImportService
import uk.co.mc.core.RelationshipType

class BootStrap {

    def importService

    def init = { servletContext ->

        RelationshipType.initDefaultRelationshipTypes()

        importService.importData()

    }
    def destroy = {
    }
}
