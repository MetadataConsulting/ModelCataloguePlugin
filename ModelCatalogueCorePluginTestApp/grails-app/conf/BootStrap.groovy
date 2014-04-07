class BootStrap {

    def importService
    def domainModellerService
    def initCatalogueService

    def init = { servletContext ->

        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
        initCatalogueService.initDefaultMeasurementUnits()

        environments {
            development {
                importService.importData()
                //domainModellerService.modelDomains()
            }
        }

    }
    def destroy = {
    }
}
