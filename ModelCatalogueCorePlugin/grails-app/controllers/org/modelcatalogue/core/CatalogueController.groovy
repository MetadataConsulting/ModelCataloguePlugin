package org.modelcatalogue.core

import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.http.HttpStatus

class CatalogueController {

    def classificationService
    def modelService

    def xref() {
        String resource = params.resource
        Long id = params.long('id')
        Integer version = params.int('version')

        CatalogueElement element

        if (version && version != 1) {
            Long lastVersion = id
            element = CatalogueElement.where {
                versionNumber == version && latestVersionId == lastVersion
            }.get()
        } else {
            element = CatalogueElement.get(id)
        }
        if (!element) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=${element.name.replaceAll(/\s+/, '_')}.xml")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(classificationService, modelService)
            printer.bind(element).writeTo(response.writer)
            return
        }


        redirect controller: resource, action: 'show', id: element.id
    }

}
