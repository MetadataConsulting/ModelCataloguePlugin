import grails.rest.render.RenderContext
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer

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

        xlsxListRenderer.registerRowWriter {
            title "Generic Excel Export"
            headers 'Type', 'Source', 'Destination'
            when { ListWrapper container, RenderContext context ->
                Relationship.isAssignableFrom(container.itemType)
            } then { Relationship rel ->
                [[rel.relationshipType.name, rel.source.name, rel.destination.name]]
            }
        }

        xlsxListRenderer.registerRowWriter('COSD') {
            title 'Export All to COSD'
            headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "Data item No.","Schema Specification","Data Dictionary Element", "Current Collection", "Format"
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && DataElement.isAssignableFrom(container.itemType)
            } then { DataElement element ->
                [[getParentModel(element)?.modelCatalogueId, getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name, element.modelCatalogueId, element.name, element.description, getUnitOfMeasure(element), getDataType(element), "-", element.ext.get("Data item No."), element.ext.get("Schema Specification"), element.ext.get("Data Dictionary Element"), element.ext.get("Current Collection"), element.ext.get("Format") ]]
            }
        }

        xlsxListRenderer.registerRowWriter('NHIC') {
            title "Export All to NHIC"
            headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "NHIC_Identifier","Link_to_existing_definition", "Notes_from_GD_JCIS" ,"Optional_Local_Identifier","A" ,"B","C" ,"D" ,"E" ,"F" ,"G","H","E2", "System", "Comments", "Group"
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && DataElement.isAssignableFrom(container.itemType)
            } then { DataElement element ->
                [[getParentModel(element)?.modelCatalogueId, getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name, element.modelCatalogueId, element.name, element.description, getUnitOfMeasure(element), getDataType(element), "-", element.ext.NHIC_Identifier, element.ext.Link_to_existing_definition, element.ext.Notes_from_GD_JCIS , element.ext.Optional_Local_Identifier, element.ext.A, element.ext.B, element.ext.C , element.ext.D , element.ext.E , element.ext.F , element.ext.G, element.ext.H, element.ext.E2, element.ext.System, element.ext.Comments, element.ext.Group]]
            }
        }

        reportsRegistry.register {
            title 'Export All to COSD'
            type Model
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'COSD'], id: true
        }

        reportsRegistry.register {
            title 'Export All to NHIC'
            type Model
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'NHIC'], id: true
        }

        reportsRegistry.register {
            title 'Export All to XML'
            type Model
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xml'], id: true
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



    def getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return null
    }

    def getParentModel(DataElement dataElement){
        Model containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    def getValueDomain(DataElement dataElement){
        if(dataElement.instantiatedBy) {
            return dataElement.instantiatedBy.first()
        }
        return null
    }

    def getDataType(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            DataType dataType = valueDomain.dataType
            if (dataType instanceof EnumeratedType) {
                return dataType.enumAsString
            }
            return dataType.name
        }
        return null
    }

    def getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return null
    }

}
