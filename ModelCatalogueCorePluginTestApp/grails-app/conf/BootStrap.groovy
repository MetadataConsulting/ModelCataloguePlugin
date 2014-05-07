import grails.rest.render.RenderContext
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer

class BootStrap {

    def importService
    def domainModellerService
    def initCatalogueService
    def publishedElementService

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
                def de = new DataElement(name: "testera", description:"test data architect").save()
                de.ext.metadata = "test metadata"

                Model anotherRoot = new Model(name: "Another root")
                anotherRoot.save()


                PublishedElement.list().each {
                    it.status = PublishedElementStatus.FINALIZED
                    it.save()
                }

                def withHistory = DataElement.findByName("NHS NUMBER STATUS INDICATOR CODE")

                10.times {
                    log.info "Creating archived version #${it}"
                    publishedElementService.archiveAndIncreaseVersion(withHistory)
                }

                //domainModellerService.modelDomains()
            }
        }

    }
    def destroy = {
    }
}
