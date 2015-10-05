package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import grails.util.GrailsNameUtils
import org.hibernate.FetchMode
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller


class DataModelController extends AbstractCatalogueElementController<DataModel> {

    DataClassService dataClassService

	DataModelController() {
		super(DataModel, false)
	}

	def report() {
		DataModel dataModel = DataModel.get(params.id)
		if (!dataModel) {
			notFound()
			return
		}
		render view: 'report', model: [dataModel: dataModel]
	}

	def gereport() {
		def results = getDataClassesForDataModel(params.id as Long)

		def dataTypes = new TreeSet<DataType>([compare: { DataType a, DataType b ->
				a?.name <=> b?.name
			}] as Comparator<DataType>)

		if (!results) {
			notFound()
			return
		}

		render view: 'gereport', model: ['models': results, 'dataTypes': dataTypes]
    }

    def content() {
        DataModel dataModel = DataModel.get(params.id)
        if (!dataModel) {
            notFound()
            return
        }

        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel>of(dataModel), ImmutableSet.<DataModel>of())

        Map<String, Integer> stats = dataModelService.getStatistics(filter)

        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true, status: 'active'])

        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            List<Map> contentDescriptors = []

            contentDescriptors << createContentDescriptor(dataModel, 'Data Classes', DataClass, dataClasses.total)
            contentDescriptors << createContentDescriptor(dataModel, 'Data Types', DataType, stats["totalDataTypeCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Measurement Units', MeasurementUnit, stats["totalMeasurementUnitCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Assets', Asset, stats["totalAssetCount"])

            contentDescriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    private static Map createContentDescriptor(DataModel dataModel, String name, Class clazz, long count) {
        String link = "/${GrailsNameUtils.getPropertyName(clazz)}?status=active&toplevel=true&dataModel=${dataModel.getId()}"
        Map ret = [:]
        ret.id = 'all'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = clazz.name
        ret.name = name
        ret.content = [count: count, itemType: clazz.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(clazz)
        ret
    }


	private Collection getDataClassesForDataModel(Long dataModelId) {
		def classificationType = RelationshipType.declarationType
		def results = DataClass.createCriteria().list {
			fetchMode "extensions", FetchMode.JOIN
			fetchMode "outgoingRelationships.extensions", FetchMode.JOIN
			fetchMode "outgoingRelationships.destination.classifications", FetchMode.JOIN
			incomingRelationships {
				and {
					eq("relationshipType", classificationType)
					source { eq('id', dataModelId) }
				}
			}
		}
		return results
	}



	@Override
	protected boolean hasUniqueName() {
		true
	}

	protected bindRelations(DataModel instance, boolean newVersion, Object objectToBind) {
		if (objectToBind.declares != null) {
			for (domain in instance.declares.findAll { !(it.id in objectToBind.declares*.id) }) {
				instance.removeFromDeclares(domain)
				domain.removeFromDeclaredWithin(instance)
			}
			for (domain in objectToBind.declares) {
				CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
				instance.addToDeclares catalogueElement
				catalogueElement.addToDeclaredWithin instance
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
}
