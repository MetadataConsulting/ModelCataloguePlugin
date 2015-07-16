package org.modelcatalogue.core

import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter
import net.sf.jasperreports.engine.util.JRLoader
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput

import org.hibernate.FetchMode
import org.springframework.core.io.Resource


class ClassificationController<T> extends AbstractCatalogueElementController<Classification> {

	ClassificationController() {
		super(Classification, false)
	}

	def report() {
		Classification classification = Classification.get(params.id)
		if (!classification) {
			notFound()
			return
		}
		render view: 'report', model: [classification: classification]
	}

	def gereport() {
		def results = getModelsForClassification(params.id as Long)

		def valueDomains = new TreeSet<ValueDomain>([compare: { ValueDomain a, ValueDomain b ->
				a?.name <=> b?.name
			}] as Comparator<ValueDomain>)

		if (!results) {
			notFound()
			return
		}

		render view: 'gereport', model: ['models': results, 'valueDomains': valueDomains]
	}


	private Collection getModelsForClassification(Long classificationId) {
		def classificationType = RelationshipType.findByName('classification')
		def results = Model.createCriteria().list {
			fetchMode "extensions", FetchMode.JOIN
			fetchMode "outgoingRelationships.extensions", FetchMode.JOIN
			fetchMode "outgoingRelationships.destination.classifications", FetchMode.JOIN
			incomingRelationships {
				and {
					eq("relationshipType", classificationType)
					source { eq('id', classificationId) }
				}
			}
		}
		return results
	}



	@Override
	protected boolean hasUniqueName() {
		true
	}

	protected bindRelations(Classification instance, boolean newVersion, Object objectToBind) {
		if (objectToBind.classifies != null) {
			for (domain in instance.classifies.findAll { !(it.id in objectToBind.classifies*.id) }) {
				instance.removeFromClassifies(domain)
				domain.removeFromClassifications(instance)
			}
			for (domain in objectToBind.classifies) {
				CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
				instance.addToClassifies catalogueElement
				catalogueElement.addToClassifications instance
			}
		}
	}

	@Override
	protected getIncludeFields() {
		def fields = super.includeFields
		fields.removeAll(['classifies'])
		fields
	}

	@Override
	protected Classification createResource() {
		Classification instance = resource.newInstance()
		bindData instance, getObjectToBind(), [include: includeFields]
		instance
	}
}
