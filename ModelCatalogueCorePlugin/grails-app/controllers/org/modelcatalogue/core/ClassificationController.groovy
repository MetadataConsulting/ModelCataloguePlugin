

package org.modelcatalogue.core
import org.springframework.core.io.Resource;
import grails.converters.XML
import grails.util.Holders;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.design.JasperDesign
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.groovy.grails.io.support.FileSystemResource

import org.hibernate.FetchMode;
import org.modelcatalogue.core.util.RelationshipDirection


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
	/*	def fileFormat= JasperExportFormat.HTML_FORMAT
		if (params.jasperFormat=="docx"){
			fileFormat=JasperExportFormat.DOCX_FORMAT
		}else if (params.jasperFormat=="pdf"){
			fileFormat=JasperExportFormat.PDF_FORMAT
		}*/

		def reportsPath=new File(grailsApplication.config.jasper.dir.reports)
		log.info "Reports path: "+reportsPath.absolutePath
		if (!reportsPath.isDirectory()){
			//means that is in the relative to web-app , or should be
			reportsPath=new File(grailsApplication.mainContext.servletContext.getRealPath(grailsApplication.config.jasper.dir.reports))
		}

		def valueDomains = new TreeSet<ValueDomain>([compare: { ValueDomain a, ValueDomain b ->
				a?.name <=> b?.name
			}] as Comparator<ValueDomain>)

		//generate jasper report
		Map parameters = new HashMap();
		parameters.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION, sessionFactory.currentSession);
		parameters.put("DOCUMENT_TITLE", classification.name);
		parameters.put("DOCUMENT_VERSION", "DRAFT");
		parameters.put("DOCUMENT_EXTENSION", "docx");
		parameters.put("SUBREPORT_DATA_SOURCE", new JRBeanCollectionDataSource(models));
		parameters.put("VALUE_DOMAINS", valueDomains);
		parameters.put("DOCUMENT_EXTENSION", valueDomains);


		
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(getReport("ClassificationInventoryGe").inputStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,new JRBeanCollectionDataSource([new Object()]))

		JRDocxExporter exporter = new JRDocxExporter();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

		exporter.exportReport();

		//we have the report into memory now 
		byte[]content = baos.toByteArray();

		response.addHeader("Content-disposition", "inline; filename="+"\"${classification.name}.docx\"")
		//response.contentType = reportDef.fileFormat.mimeTyp
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
		
		throw new Exception("No such report spec: ${path} (jasper or .jrxml)")

	}

	private String getFilePath(String name) {

		if (Holders.config.jasper.dir.reports) {
			return Holders.config.jasper.dir.reports + File.separator + FilenameUtils.getPath(name) + FilenameUtils.getBaseName(name)
		}
		return "/JReports" + File.separator + FilenameUtils.getPath(name) + FilenameUtils.getBaseName(name)
	}

	private Collection getModelsForClassification(Long classificationId) {
		def mapElements= new HashMap();
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
