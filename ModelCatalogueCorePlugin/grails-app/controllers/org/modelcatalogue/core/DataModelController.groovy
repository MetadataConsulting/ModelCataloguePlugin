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


class DataModelController extends AbstractCatalogueElementController<DataModel> {

	DataModelController() {
		super(DataModel, false)
	}

	def report() {
		DataModel classification = DataModel.get(params.id)
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
		DataModel classification = DataModel.get(params.id)
		def models = getModelsForClassification(params.id as Long)
		
		if (!models) {
			notFound()
			return
		}

	
		if (!params.jasperFormat){
			params.jasperFormat="docx"
		}

		byte[] content = getClassificationReportAsByte(classification,  models,params.jasperFormat)

		response.addHeader("Content-disposition", "inline; filename="+"\"${classification.name}.${params.jasperFormat}\"")
		response.characterEncoding = "UTF-8"
		response.setContentLength(content.length)
		response.outputStream << content
		response.outputStream.flush()
		response.flushBuffer()
	}

	/**
	 * Get classification report in pdf or docx format
	 * @param classification
	 * @param models
	 * @param fileExtension only pdf and docx are supported
	 * @return a byte array containing the result 
	 */
	private byte[] getClassificationReportAsByte(DataModel classification, Collection models,String fileExtension) {
		Resource resource=getApplicationContext().getResource('classpath:/jReports/ClassificationInventoryGe.jasper')
		
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(resource.inputStream)
		def  exporter

		if (fileExtension=="pdf"){
			exporter = new JRPdfExporter()
		}else{
			exporter = new JRDocxExporter()
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
		parameters.put("REPORTS_PATH", resource.getFile().parent)



		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,new JRBeanCollectionDataSource([new Object()]))
		ByteArrayOutputStream baos = new ByteArrayOutputStream()
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos))
		exporter.exportReport()

		//we have the report into memory now
		return baos.toByteArray()
	}

	private Collection getModelsForClassification(Long classificationId) {
		def classificationType = RelationshipType.findByName('classification')
		def results = DataClass.createCriteria().list {
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

	protected bindRelations(DataModel instance, boolean newVersion, Object objectToBind) {
		if (objectToBind.defines != null) {
			for (domain in instance.defines.findAll { !(it.id in objectToBind.defines*.id) }) {
				instance.removeFromDefines(domain)
				domain.removeFromDefinedWithin(instance)
			}
			for (domain in objectToBind.defines) {
				CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
				instance.addToDefines catalogueElement
				catalogueElement.addToDefinedWithin instance
			}
		}
	}

	@Override
	protected getIncludeFields() {
		def fields = super.includeFields
		fields.removeAll(['defines'])
		fields
	}

	@Override
	protected DataModel createResource() {
		DataModel instance = resource.newInstance()
		bindData instance, getObjectToBind(), [include: includeFields]
		instance
	}
}
