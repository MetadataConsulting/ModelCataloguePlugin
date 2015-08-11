package org.modelcatalogue.core.gel

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataType

import java.util.concurrent.ExecutorService
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
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.SecurityService;
import org.modelcatalogue.core.api.ElementStatus;
import org.modelcatalogue.core.audit.AuditService
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus;

/**
 * Various reports generations as an asset. 
 *  
 *
 */
class ClassificationReportsController {
    ExecutorService executorService
    AuditService auditService
    AssetService assetService
    SecurityService modelCatalogueSecurityService
    
    def index() { }
    
    def gereportDoc() {
        DataModel classification = DataModel.get(params.id)
        def models = getDataClassesForDataModels(params.id as Long)
        

        if (!models) {
            render status: HttpStatus.NOT_FOUND
            return
        }

    
        if (!params.jasperFormat){
            params.jasperFormat="docx"
        }


        def assetName="$classification.name report as ${params.jasperFormat} "
        def assetFileName="${classification.name}-${classification.status}-${classification.version}.${params.jasperFormat}"


        def assetPendingDesc="Your classification report  will be available in this asset soon. Use Refresh action to reload"
        def assetFinalizedDesc="Your classification is ready. Use Download button to download it."
        def assetErrorDesc="Error generating classification report"
        def assetMimeType="application/${params.jasperFormat}"

        def assetId=storeAssetFromJasper(classification,models,params,assetName,assetMimeType,assetPendingDesc,assetFinalizedDesc,assetErrorDesc,assetFileName)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def storeAssetFromJasper(DataModel classification,Collection models,Map params,String assetName=null,String mimeType="application/octet-stream",String assetPendingDesc="",String assetFinalizedDesc="",String assetErrorDesc="",String originalFileName="unknown"){
        Asset asset = new Asset(
                name:assetName ,
                originalFileName: originalFileName,
                description: assetPendingDesc,
                status: ElementStatus.PENDING,
                contentType: mimeType,
                size: 0
                )

        asset.save(flush: true, failOnError: true)

        Long id = asset.id
        Long authorId = modelCatalogueSecurityService.currentUser?.id

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                Asset updated = Asset.get(id)
                try {
                    //do the hard work
                   
                        byte[] result=getClassificationReportAsByte(classification,models,params.jasperFormat)
                        assetService.storeAssetFromInputStream( new ByteArrayInputStream(result),mimeType, updated)

                   

                    updated.status = ElementStatus.FINALIZED
                    updated.description = assetFinalizedDesc
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = assetErrorDesc+":$e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
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

        def dataTypes = new TreeSet<DataType>([compare: { DataType a, DataType b ->
                a?.name <=> b?.name
            }] as Comparator<DataType>)

        

        //generate jasper report
        Map parameters = new HashMap()
        parameters.put("DOCUMENT_TITLE", classification.name)
        parameters.put("DOCUMENT_FILENAME",reportFileName )
        parameters.put("DOCUMENT_STATUS", classification.status)
        parameters.put("SUBREPORT_DATA_SOURCE", new JRBeanCollectionDataSource(models))
        parameters.put("DATA_TYPES", dataTypes)
        parameters.put("REPORTS_PATH", resource.getFile().parent)



        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,new JRBeanCollectionDataSource([new Object()]))
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos))
        exporter.exportReport()

        //we have the report into memory now
        return baos.toByteArray()
    }

    private Collection getDataClassesForDataModels(Long classificationId) {
        def classificationType = RelationshipType.declarationType
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
    
    
    
}
