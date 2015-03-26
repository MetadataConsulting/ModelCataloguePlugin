package org.modelcatalogue.core


import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.export.JRXlsExporter
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory
import net.sf.jasperreports.engine.util.JRLoader
import net.sf.jasperreports.export.Exporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput

import org.apache.commons.io.FilenameUtils
import org.hibernate.FetchMode
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource


class ClassificationController<T> extends AbstractCatalogueElementController<Classification> {
	def sessionFactory

	//def relationshipService

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


	def gereportDoc() {
		Classification classification = Classification.get(params.id)
		def leafs = new ArrayList()
		def models = getModelsForClassification(params.id as Long)
		
		if (!models) {
			notFound()
			return
		}

		
		
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(getReport("ClassificationInventoryGe").inputStream)
		Exporter exporter = new JRDocxExporter()
		def fileExtension=".docx"
		
		if (params.jasperFormat=="pdf"){
			exporter = new JRPdfExporter()
			fileExtension=".pdf"
		}

		def reportFileName="${classification.name}-${classification.status}-${classification.version}${fileExtension}"

		def valueDomains = new TreeSet<ValueDomain>([compare: { ValueDomain a, ValueDomain b ->
				a?.name <=> b?.name
			}] as Comparator<ValueDomain>)

		//generate jasper report
		Map parameters = new HashMap()
		parameters.put("DOCUMENT_TITLE", classification.name)
		parameters.put("DOCUMENT_FILENAME",reportFileName )
		parameters.put("DOCUMENT_STATUS", classification.status)
		parameters.put("SUBREPORT_DATA_SOURCE", new JRBeanCollectionDataSource(models))
		parameters.put("VALUE_DOMAINS", valueDomains)


		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,new JRBeanCollectionDataSource([new Object()]))


		ByteArrayOutputStream baos = new ByteArrayOutputStream()

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos))
		exporter.exportReport()

		//we have the report into memory now
		byte[]content = baos.toByteArray()

		response.addHeader("Content-disposition", "inline; filename="+"\"${classification.name}${fileExtension}\"")
		response.characterEncoding = "UTF-8"
		response.setContentLength(content.length)
		response.outputStream << content
		response.outputStream.flush()
		response.flushBuffer()
	}



	private Resource getReport(String name) {
		String path = getFilePath(name)

		Resource result = getApplicationContext().getResource(path + ".jasper")
		if (result.exists()) {
			return result
		}


		result = new FileSystemResource(path + ".jasper")
		if (result.exists()) {
			return result
		}

		throw new Exception("No such report spec: ${path} (.jasper)")

	}

	private String getFilePath(String name) {

		if (grailsApplication.config.jasper.dir.reports) {
			return grailsApplication.config.jasper.dir.reports + File.separator + FilenameUtils.getPath(name) + FilenameUtils.getBaseName(name)
		}
		return "/JReports" + File.separator + FilenameUtils.getPath(name) + FilenameUtils.getBaseName(name)
	}

	private Collection getModelsForClassification(Long classificationId) {
		def mapElements= new HashMap()
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
