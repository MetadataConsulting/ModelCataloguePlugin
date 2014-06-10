import grails.rest.render.RenderContext
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.modelcatalogue.core.*

class BootStrap {

    def importService
    def domainModellerService
    def initCatalogueService
    def publishedElementService

    XLSXListRenderer xlsxListRenderer
    ReportsRegistry reportsRegistry

    def init = { servletContext ->

        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
        initCatalogueService.initDefaultMeasurementUnits()

        xlsxListRenderer.registerRowWriter('reversed') {
            title "Reversed DEMO Export"
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

                15.times {
                    new Model(name: "Another root #${String.format('%03d', it)}").save()
                }

                def parentModel1 = Model.findByName("Another root #001")

                15.times{
                    def child = new Model(name: "Another root #${String.format('%03d', it)}").save()
                    parentModel1.addToParentOf(child)
                }


                for (DataElement element in DataElement.list()) {
                    parentModel1.addToContains element
                }


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
