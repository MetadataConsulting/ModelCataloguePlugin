package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.http.HttpStatus
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


class CatalogueController {

    def dataModelService
    def dataClassService
    def elementService

    def xref() {
        CatalogueElement element = elementService.findByModelCatalogueId(CatalogueElement, request.forwardURI)

        if (!params.resource || !element) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element).writeTo(response.writer)
            return
        }

        if (params.format == 'xsd') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.dump(element, "data/${element.name}.xml")
            def transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File("data/ModelCat2Schema.xsl")))
            def xml= new File("data/${element.name}.xml").getText()
            PipedInputStream input = new PipedInputStream()
            StringWriter outWriter = new StringWriter();
            try{
               transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(outWriter))
            }
            catch( Throwable e){
                log.debug("XSL Transformatin problem:" + e.message())
            }finally{
                outWriter.close()
            }
            response.writer.write(outWriter.toString())
            return
        }

        redirect controller: params.resource, action: 'show', id: element.id
    }

    def ext() {
        String key = params.key
        String value = params.value

        DetachedCriteria<CatalogueElement> criteria = new DetachedCriteria<CatalogueElement>(CatalogueElement)
        criteria = criteria.build {
            extensions {
                eq 'name', key
                eq 'extensionValue', value
            }
            sort('versionNumber', 'desc')
        }

        List<CatalogueElement> elements = criteria.list()

        if (!elements) {
            render status: HttpStatus.NOT_FOUND
            return
        }


        CatalogueElement element = elements.first()

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element).writeTo(response.writer)
            return
        }

        redirect url: "${grailsApplication.config.grails.serverURL}/catalogue/${GrailsNameUtils.getPropertyName(HibernateHelper.getEntityClass(element))}/${element.id}"
    }

}
