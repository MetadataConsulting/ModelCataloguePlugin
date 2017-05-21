package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import grails.util.GrailsNameUtils
import org.hibernate.SessionFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.modelcatalogue.gel.export.GridReportXlsxExporter
import org.springframework.http.HttpStatus

class DataModelController extends AbstractCatalogueElementController<DataModel> {

    DataClassService dataClassService
    DataElementService dataElementService
    DataTypeService dataTypeService
    SessionFactory sessionFactory

	DataModelController() {
		super(DataModel, false)
	}

    //can be used in the future
    static final String DOC_IMAGE_PATH = ""

    //custom template is better than the default inventory report - so using it
    static Closure customTemplate = {
        'document' font: [family: 'Calibri', size: 11], margin: [left: 20, right: 10]
        'paragraph.title' font: [color: '#1F497D', size: 32.pt, bold: true], margin: [top: 150.pt, bottom: 10.pt]
        'paragraph.subtitle' font: [color: '#1F497D', size: 36.pt], margin: [top: 0.pt]
        'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
        'heading1' font: [size: 18, bold: true]
        'heading2' font: [size: 18, bold: true]
        'heading3' font: [size: 16, bold: true]
        'heading4' font: [size: 16, bold: true]
        'heading5' font: [size: 15]
        'heading6' font: [size: 14]
        'table.row.cell.headerCell' font: [color: '#FFFFFF', size: 12.pt, bold: true], background: '#1F497D'
        'table.row.cell' font: [size: 10.pt]
        'paragraph.headerImage' height: 1.366.inches, width: 2.646.inches
    }


    def inventorySpreadsheet(String name, Integer depth) {
        DataModel dataModel = DataModel.get(params.id)

        def dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            CatalogueElementToXlsxExporter.forDataModel(DataModel.get(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    //produce a grid report spreadsheet where the whole data set is diaplyed as a grid with metadata and relationships (rather then tabs)

    def gridSpreadsheet(String name, Integer depth) {
        DataModel dataModel = DataModel.get(params.id)

        def dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            GridReportXlsxExporter.create(DataModel.get(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def inventoryDoc(String name, Integer depth) {

        DataModel dataModel = DataModel.get(params.id)

        def modelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

            def assetId =  assetService.storeReportAsAsset(
                    dataModel,
                    name: name ? name : "${dataModel.name} report as MS Excel Document",
                    originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.docx",
                    contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ) { OutputStream out ->
                new DataModelToDocxExporter(DataModel.get(modelId), dataClassService, elementService, customTemplate, DOC_IMAGE_PATH, depth).export(out)
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

    def reindex() {
        DataModel dataModel = DataModel.get(params.id)
        if (!dataModel) {
            notFound()
            return
        }

        Long id = dataModel.id

        executorService.submit {
            DataModel model = DataModel.get(id)
            modelCatalogueSearchService.index(model.declares).subscribe {
                log.info "${model} reindexed"
            }
        }

        respond(success: true)
    }

    def content() {
        DataModel dataModel = DataModel.get(params.id)
        if (!dataModel) {
            notFound()
            return
        }

        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel>of(dataModel), ImmutableSet.<DataModel>of())
        Map<String, Integer> stats = dataModelService.getStatistics(filter)

        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true, status: dataModel.status != ElementStatus.DEPRECATED ? 'active' : '' ])

        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            List<Map> contentDescriptors = []

            contentDescriptors << createContentDescriptor(dataModel, 'Data Classes', DataClass, dataClasses.total)
            contentDescriptors << createDataElementsByTagDescriptor(dataModel)
            contentDescriptors << createContentDescriptor(dataModel, 'Data Types', DataType, Integer.MAX_VALUE)
            contentDescriptors << createContentDescriptor(dataModel, 'Measurement Units', MeasurementUnit, stats["totalMeasurementUnitCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Business Rules', ValidationRule, stats["totalValidationRuleCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Assets', Asset, stats["totalAssetCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Tags', Tag, stats["totalTagCount"])

            if (dataModel.status != ElementStatus.DEPRECATED) {
                Map deprecatedItems = createContentDescriptor(dataModel, 'Deprecated Items', CatalogueElement, stats["deprecatedCatalogueElementCount"])
                deprecatedItems.link = deprecatedItems.link.replace('status=active', 'status=deprecated')
                deprecatedItems.content.link = deprecatedItems.link
                deprecatedItems.status = 'DEPRECATED'
                contentDescriptors << deprecatedItems
            }

            contentDescriptors << createContentDescriptorForRelationship('Imported Data Models', 'imports',  dataModel, RelationshipType.importType, RelationshipDirection.OUTGOING)

            if (params.boolean('root')) {
                contentDescriptors << createVersionsDescriptor(dataModel)
            }

            contentDescriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    def dependents() {
        DataModel dataModel = DataModel.get(params.id)
        if (!dataModel) {
            notFound()
            return
        }

        respond dataModelService.findDependents(dataModel)
    }

    private static Map createDataElementsByTagDescriptor(DataModel dataModel) {
        String link = "/tag/forDataModel/${dataModel.getId()}?status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map ret = [:]
        ret.id = 'forDataModel'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = DataElement.name
        ret.name = 'Data Elements'
        ret.content = [count: DataModelService.allTags(dataModel).size() + 2, itemType: Tag.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(DataElement)
        ret.status = dataModel.status.toString()
        ret
    }

    private static Map createContentDescriptor(DataModel dataModel, String name, Class clazz, long count) {
        String link = "/${GrailsNameUtils.getPropertyName(clazz)}?toplevel=true&dataModel=${dataModel.getId()}&status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
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

        if (objectToBind.policies != null) {
            Set<DataModelPolicy> policies = objectToBind.policies.collect { DataModelPolicy.get(it.id) } as Set<DataModelPolicy>
            Set<DataModelPolicy> existing = instance.policies ?: Collections.emptySet()
            (policies - existing).each {
                instance.addToPolicies(it)
            }
            (existing - policies).each {
                instance.removeFromPolicies(it)
            }
            FriendlyErrors.failFriendlySave(instance)
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

    def history(Integer max) {
        String name = getResourceName()
        Class type = resource

        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        Long id = element.id

        if (!element.latestVersionId) {
            respond Lists.wrap(params, "/${name}/${params.id}/history", Lists.lazy(params, type, {
                [type.get(id)]
            }, { 1 }))
            return
        }

        Long latestVersionId = element.latestVersionId

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'
        customParams.order = 'desc'

        respond Lists.fromCriteria(customParams, type, "/${name}/${params.id}/history") {
            eq 'latestVersionId', latestVersionId
        }
    }
}
