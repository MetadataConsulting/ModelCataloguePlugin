package org.modelcatalogue.core

import grails.converters.JSON
import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.http.HttpStatus

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
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element).writeTo(response.writer)
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
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element).writeTo(response.writer)
            return
        }

        redirect url: "${grailsApplication.config.grails.serverURL}/catalogue/${GrailsNameUtils.getPropertyName(HibernateHelper.getEntityClass(element))}/${element.id}"
    }


    def feedback(String key) {
        render(BuildProgressMonitor.get(key) as JSON)
    }

    def feedbacks() {
        if (params.max) {
            params.max = params.long('max')
        }
        render(Lists.lazy(params, ProgressMonitor, '/feedback', {
            CacheService.MONITORS_CACHE.asMap().entrySet().sort{ a, b -> -(a.value.lastUpdated <=> b.value.lastUpdated) }.collect { [key: it.key, name: it.value.name, status: it.value.status.toElementStatusEquivalent().toString(), ] }
        }, {
            CacheService.MONITORS_CACHE.size()
        }) as JSON)
    }

}
