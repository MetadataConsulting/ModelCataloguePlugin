package org.modelcatalogue.core

import grails.converters.XML
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.design.JasperDesign
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat;
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService;
import org.hibernate.FetchMode;
import org.modelcatalogue.core.util.RelationshipDirection


class ClassificationController<T> extends AbstractCatalogueElementController<Classification> {
    JasperService jasperService
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
        def leafs = new ArrayList()
        def models = getModelsForClassification(params.id as Long)

        if (!models) {
            notFound()
            return
        }

        //generate jasper report
        def reportDef = new JasperReportDef(name: 'ClassificationInventoryGe',
                fileFormat: JasperExportFormat.HTML_FORMAT,
                reportData: models
        )
        def content = jasperService.generateReport(reportDef).toByteArray()

		//response.addHeader("Content-Type", "application/msword; charset=utf-8")
        //response.addHeader("Content-disposition", "inline; filename="+"\"${classification.name}.${reportDef.fileFormat.extension}\"")
		response.contentType = reportDef.fileFormat.mimeTyp
		response.characterEncoding = "UTF-8"
        response.setContentLength(content.length)
        response.outputStream << content
        response.outputStream.flush()
        response.flushBuffer()
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
                    //isNull('destination')
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
