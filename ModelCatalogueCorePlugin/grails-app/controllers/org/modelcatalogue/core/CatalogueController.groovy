package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.util.Legacy
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.http.HttpStatus

class CatalogueController {

    def dataModelService
    def dataClassService

    def xref() {
        String resource = params.resource

        if (Legacy.isLegacyResourceName(resource)) {
            redirect permanent: true, url: Legacy.getRedirectUrl(Legacy.getNewResourceName(resource), request)
            return
        }

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
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element).writeTo(response.writer)
            return
        }


        redirect controller: resource, action: 'show', id: element.id
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
            response.setHeader("Content-disposition", "attachment; filename=${element.name.replaceAll(/\s+/, '_')}.xml")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element).writeTo(response.writer)
            return
        }

        redirect url: "${grailsApplication.config.grails.serverURL}/catalogue/${GrailsNameUtils.getPropertyName(HibernateProxyHelper.getClassWithoutInitializingProxy(element))}/${element.id}"
    }

}
