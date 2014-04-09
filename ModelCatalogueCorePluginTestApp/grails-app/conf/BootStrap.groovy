import grails.rest.render.RenderContext
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer

class BootStrap {

    def importService
    def domainModellerService
    def initCatalogueService
    XLSXListRenderer xlsxListRenderer

    def init = { servletContext ->

        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
        initCatalogueService.initDefaultMeasurementUnits()

        xlsxListRenderer.registerRowWriter('reversed') {
            headers 'Description', 'Name', 'ID'
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search'] && CatalogueElement.isAssignableFrom(container.itemType)
            } then { CatalogueElement element ->
                [[element.description, element.name, element.id]]
            }
        }


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
