package org.modelcatalogue.core

import org.hibernate.FetchMode


class DataModelController extends AbstractCatalogueElementController<DataModel> {

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
