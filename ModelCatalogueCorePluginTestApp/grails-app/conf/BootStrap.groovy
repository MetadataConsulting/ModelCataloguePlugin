import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.RelationshipType

class BootStrap {

    def importService
    def domainModellerService

    def init = { servletContext ->

        RelationshipType.initDefaultRelationshipTypes()
        DataType.initDefaultDataTypes()

        environments {
            development {
                //importService.importData()
                //domainModellerService.modelDomains()
            }
        }

    }
    def destroy = {
    }
}
