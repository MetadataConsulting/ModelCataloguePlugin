package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import grails.util.GrailsNameUtils
import org.hibernate.FetchMode
import org.modelcatalogue.core.export.inventory.DataModelToXlsxExporter
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

class DataModelController extends AbstractCatalogueElementController<DataModel> {

    DataClassService dataClassService

	DataModelController() {
		super(DataModel, false)
	}

    def inventorySpreadsheet(String name, Integer exportDepth) {
        DataModel dataModel = DataModel.get(params.id)

        def dataModelId = dataModel.id
        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            def exporter = new DataModelToXlsxExporter(dataClassService: dataClassService,
                dataModel: DataModel.get(dataModelId), exportDepth: exportDepth)
            exporter.export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

	def containsOrImports() {
        DataModel dataModel = DataModel.get(params.id)
        if (!dataModel) {
            notFound()
            return
        }

        CatalogueElement other = CatalogueElement.get(params.other)
        if (!other) {
            notFound()
            return
        }

        if (!other.dataModel) {
            respond(success: false, contains: false, imports: false)
            return
        }

        if (dataModel == other.dataModel) {
            respond(success: true, contains: true, imports: false)
            return
        }

        DataModelFilter filter = DataModelFilter.includes(dataModel).withImports()


        if (filter.isIncluding(other.dataModel)) {
            respond(success: true, contains: false, imports: true)
            return
        }
        respond(success: false, contains: false, imports: false)
    }

    def content() {
        DataModel dataModel = DataModel.get(params.id)
        if (!dataModel) {
            notFound()
            return
        }

        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel>of(dataModel), ImmutableSet.<DataModel>of())
        Map<String, Integer> stats = dataModelService.getStatistics(filter)

        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true])

        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            List<Map> contentDescriptors = []

            contentDescriptors << createContentDescriptor(dataModel, 'Data Classes', DataClass, dataClasses.total)
            contentDescriptors << createContentDescriptor(dataModel, 'Data Elements', DataElement, stats["totalDataElementCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Data Types', DataType, stats["totalDataTypeCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Measurement Units', MeasurementUnit, stats["totalMeasurementUnitCount"])

            Map assets = createContentDescriptor(dataModel, 'Assets', Asset, stats["totalAssetCount"])
            assets.link = "${assets.link}&status=active"
            assets.content.link = assets.link
            contentDescriptors << assets

            contentDescriptors << createContentDescriptorForRelationship('Imported Data Models', 'imports',  dataModel, RelationshipType.importType, RelationshipDirection.OUTGOING)

            if (params.boolean('root')) {
                contentDescriptors << createVersionsDescriptor(dataModel)
            }

            contentDescriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    private static Map createContentDescriptor(DataModel dataModel, String name, Class clazz, long count) {
        String link = "/${GrailsNameUtils.getPropertyName(clazz)}?toplevel=true&dataModel=${dataModel.getId()}"
        Map ret = [:]
        ret.id = 'all'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = clazz.name
        ret.name = name
        ret.content = [count: count, itemType: clazz.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(clazz)
		ret.status = dataModel.status.toString()
        ret
    }

    private static Map createVersionsDescriptor(DataModel dataModel) {
        String link = "/dataModel/${dataModel.getId()}/history"
        Map ret = [:]
        ret.id = link
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = "${DataModel.name}.Versions"
        ret.name = 'Versions'
        ret.content = [count: dataModel.countVersions(), itemType: DataModel.name, link: link]
        ret.link = link
		ret.status = dataModel.status.toString()
        ret
    }

	private static Map createContentDescriptorForRelationship(String name, String property, DataModel dataModel, RelationshipType relationshipType, RelationshipDirection direction) {
		String link = "/dataModel/${dataModel.getId()}/${direction.actionName}/${relationshipType.name}"
		Map ret = [:]
		ret.id = link
		ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
		ret.elementType = Relationships.name
		ret.name = name
		ret.content = [count: dataModel.countRelationshipsByDirectionAndType(direction, relationshipType), itemType: Relationship.name, link: link]
		ret.link = link
        ret.relationshipType = relationshipType
        ret.direction = direction.actionName
		ret.status = dataModel.status.toString()
        ret.element = dataModel
        ret.property = property

		ret
	}

	private Collection getDataClassesForDataModel(Long dataModelId) {
		def results = DataClass.createCriteria().list {
			fetchMode "extensions", FetchMode.JOIN
			fetchMode "outgoingRelationships.extensions", FetchMode.JOIN
			fetchMode "outgoingRelationships.destination.classifications", FetchMode.JOIN
            dataModel {
                eq 'id', dataModelId
            }
		}
		return results
	}



	@Override
	protected boolean hasUniqueName() {
		true
	}

    @Override
    protected boolean isFavoriteAfterUpdate() {
        return true
    }

    protected bindRelations(DataModel instance, boolean newVersion, Object objectToBind) {
		if (objectToBind.declares != null) {
			for (domain in instance.declares.findAll { !(it.id in objectToBind.declares*.id) }) {
				domain.dataModel = null
                FriendlyErrors.failFriendlySave(domain)
			}
			for (domain in objectToBind.declares) {
				CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
                catalogueElement.dataModel = instance
                FriendlyErrors.failFriendlySave(catalogueElement)
			}
		}
	}

	@Override
	protected getIncludeFields() {
		def fields = super.includeFields
		fields.removeAll(['declares'])
		fields
	}

	@Override
	protected DataModel createResource() {
		DataModel instance = resource.newInstance()
		bindData instance, getObjectToBind(), [include: includeFields]
		instance
	}

    protected String getHistoryOrderDirection() {
        'asc'
    }

    protected String getHistorySortProperty() {
        'semanticVersion'
    }
}
